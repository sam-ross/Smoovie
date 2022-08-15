package com.sam.ross.smoovie.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sam.ross.smoovie.dao.MovieDao;
import com.sam.ross.smoovie.objects.IMDbMovie;
import com.sam.ross.smoovie.objects.IMDbSearchResponse;
import com.sam.ross.smoovie.objects.subtitles.DownloadRequestResponse;
import com.sam.ross.smoovie.objects.subtitles.SubtilesSearchResponse;
import com.sam.ross.smoovie.objects.subtitles.Subtitles;
import com.sam.ross.smoovie.objects.words.WordData;
import com.sam.ross.smoovie.objects.words.WordList;
import com.sam.ross.smoovie.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;

@Service
public class MovieService {
    @Autowired
    private MovieDao dao;

    public List<IMDbMovie> searchIMDbMovies(String title, String apiKey) throws IOException, InterruptedException {
        String responseBody = dao.searchIMDbMovies(title, apiKey);
        ObjectMapper mapper = new ObjectMapper();
        IMDbSearchResponse searchResponse = mapper.readValue(responseBody, IMDbSearchResponse.class);

        return searchResponse.getResults();
    }

    public WordList getWordList(String imdbId, String apiKey) throws IOException, InterruptedException {
        if (imdbId.charAt(0) == 't' && imdbId.charAt(1) == 't') {
            imdbId = imdbId.substring(2);
        }
        System.out.println(imdbId);

        // get file id
        String fileId = getFileId(imdbId, apiKey);
        System.out.println(fileId);

        // get download link
        String downloadLink = getDownloadLink(fileId, apiKey);
        System.out.println(downloadLink);

        // use download link
        String subtitlesSRT = downloadSubtitles(downloadLink);

        // parse subtitles
        List<String> words = parseSubtitleFile(subtitlesSRT);

        Subtitles subtitles = Subtitles.builder()
                .subtitlesSRT(subtitlesSRT)
                .words(words)
                .build();

        // return subtitles
        return WordList.builder()
                .words(words)
                .build();
    }

    private String getFileId(String imdbId, String apiKey) throws IOException, InterruptedException {
        String responseBody = dao.searchForSubtitles(imdbId, apiKey);
        ObjectMapper mapper = new ObjectMapper();
        SubtilesSearchResponse subtilesSearchResponse = mapper.readValue(responseBody, SubtilesSearchResponse.class);

        // first file id
        return subtilesSearchResponse.getData().get(0).get("attributes").get("files").get(0).get("file_id").asText();
    }

    private String getDownloadLink(String fileId, String apiKey) throws IOException, InterruptedException {
        String responseBody = dao.requestForDownload(fileId, apiKey);
        ObjectMapper mapper = new ObjectMapper();
        DownloadRequestResponse downloadRequestResponse = mapper.readValue(responseBody, DownloadRequestResponse.class);

        return downloadRequestResponse.getLink();
    }

    private String downloadSubtitles(String downloadLink) throws IOException, InterruptedException {
        String responseBody = dao.useDownloadLink(downloadLink);
        System.out.println(responseBody);

        return responseBody;
    }

    private List<String> parseSubtitleFile(String subtitlesSRT) {
        Matcher matcher = Pattern.compile("(?<order>\\d+)\\n(?<start>[\\d:,]+)\\s+-{2}\\>\\s+(?<end>[\\d:,]+)\\n" +
                "(?<text>[\\s\\S]*?(?=\\n{2}|$))").matcher(subtitlesSRT);

        StringBuilder fullSubtitlesBuilder = new StringBuilder();
        while (matcher.find()) {
            fullSubtitlesBuilder.append("\n" + matcher.group("text"));
        }

        // start check
        int endOfFirstLine = fullSubtitlesBuilder.indexOf("\n", 1);
        int endOfSecondLine = fullSubtitlesBuilder.indexOf("\n", endOfFirstLine + 1);
        String start = fullSubtitlesBuilder.substring(0, endOfSecondLine).toLowerCase();

        if (start.contains("sub") || start.contains("caption") || start.contains("credit") || start.contains("sync")) {
            fullSubtitlesBuilder.delete(0, endOfSecondLine + 1);
        }

        // end check
        int endOfSecondLastLine = fullSubtitlesBuilder.lastIndexOf("\n");
        int endOfThirdLastLine = fullSubtitlesBuilder.substring(0, endOfSecondLastLine).lastIndexOf("\n");
        String end = fullSubtitlesBuilder.substring(endOfThirdLastLine, fullSubtitlesBuilder.length()).toLowerCase();

        if (end.contains("sub") || end.contains("caption") || end.contains("credit") || end.contains("sync")) {
            fullSubtitlesBuilder.delete(endOfThirdLastLine, fullSubtitlesBuilder.length());
        }

        String fullSubtitles = fullSubtitlesBuilder.toString();
        System.out.println(fullSubtitles);

        fullSubtitles = fullSubtitles.replaceAll("\\<.*?\\>|\\[.*?\\]|\\{.*?\\}|\\(.*?\\)", "");
        fullSubtitles = fullSubtitles.replace("―", " ").replace("–", " ").replace("-", " ");

        List<String> charactersToRemove = List.of("\"", "“", "”", ",", ".", "…", "♪", "♫", ":", ";", "?", "!", "[",
                "]", "(", ")", "<", ">", "{", "}", "#");
        for (String ch : charactersToRemove) {
            fullSubtitles = fullSubtitles.replace(ch, "");
        }

        fullSubtitles = fullSubtitles.toLowerCase();

        fullSubtitles = fullSubtitles.replace("\n", " ");
        fullSubtitles = fullSubtitles.trim().replaceAll(" +", " "); // reduces multiple spaces to one

        String[] wordsArr = fullSubtitles.split(" ");
        List<String> words = List.of(wordsArr);

        for (String word : words) {
            System.out.println(format("\"%s\"", word));
        }

        return words;
    }

    // Data time
    public WordData getWordData(List<String> words, int phraseLength, int numberOfSections) {
        HashMap<String, Integer> wordFrequencies = getWordFrequencies(words, true);
        HashMap<String, Integer> wordLengths = getWordLengths(words);
        HashMap<String, Integer> phraseFrequencies = getPhraseFrequencies(words, phraseLength);
        HashMap<String, Integer> swearWordFrequencies = getSwearWordFrequencies(words);
        HashMap<String, Integer> swearWordFrequenciesOverTime = getSwearWordFrequenciesOverTime(words, numberOfSections);

        return WordData.builder()
                .wordFrequencies(wordFrequencies)
                .wordLengths(wordLengths)
                .phraseFrequencies(phraseFrequencies)
                .swearWordFrequencies(swearWordFrequencies)
                .swearWordFrequenciesOverTime(swearWordFrequenciesOverTime)
                .build();
    }

    public HashMap<String, Integer> getWordFrequencies(List<String> words, Boolean removeCommonWords) {
        HashMap<String, Integer> hm = new HashMap<>();
        for (String word : words) {
            int value = 1;
            if (hm.containsKey(word)) {
                value += hm.get(word);
            }
            hm.put(word, value);
        }
        System.out.println(hm.entrySet());

        hm = sortByValue(hm, false);

        System.out.println(hm.entrySet());
        System.out.println(hm.size());

        System.out.println();

        if (!removeCommonWords) {
            return hm;
        }

        // if the user wants the stopwords removed (default)
        // remove stopwords
        HashMap<String, Integer> hmNoStopWords = hm;
        List<String> stopWords = Utils.getStopWords();

        hmNoStopWords.entrySet().removeIf(k -> stopWords.contains(k.getKey()));

        System.out.println(hmNoStopWords.entrySet());
        System.out.println(hmNoStopWords.size());

        System.out.println();

        return hmNoStopWords;
    }

    public HashMap<String, Integer> getWordLengths(List<String> words) {
        HashMap<String, Integer> hm = new HashMap<>();
        for (String word : words) {
            System.out.println(word.length());
            int length = word.length();
            String lengthString = String.valueOf(length);

            int value = 1;
            if (hm.containsKey(lengthString)) {
                value += hm.get(lengthString);
            }
            hm.put(lengthString, value);
        }
        hm = sortByValue(hm, false);
        return hm;
    }

    public HashMap<String, Integer> getPhraseFrequencies(List<String> words, int phraseLength) {
        // phrases
        HashMap<String, Integer> phr = new HashMap<>();
        int numberOfDuplicates = 0;

        // O(N) improved version
        for (int i = 0; i < words.size() - (phraseLength - 1); i++) {
            String phrase = String.join(" ", words.subList(i, i + phraseLength));   // indexTo is exclusive

            int value = 1;
            if (phr.containsKey(phrase)) {
                value += phr.get(phrase);
                numberOfDuplicates++;
            }
            phr.put(phrase, value);
        }

        if (numberOfDuplicates == 0) System.out.println("There were no common phrases for this size of phrase");

        phr = sortByValue(phr, true);

        System.out.println(phr.values());
        System.out.println(phr.entrySet());
        System.out.println(phr.size());

        return phr;
    }

    public HashMap<String, Integer> getSwearWordFrequencies(List<String> words) {
        // build hashmap
        HashMap<String, Integer> hm = new HashMap<>();
        for (String word : words) {
            int value = 1;
            if (hm.containsKey(word)) {
                value += hm.get(word);
            }
            hm.put(word, value);
        }
        System.out.println(hm.entrySet());
        System.out.println(hm.size());

        // swear words
        List<String> swearWords = Utils.swearWords();
        List<String> easilyMistakenSwearWords = Utils.explicityDefinedSwearWords();   // these are swear words which
        // could get mistaken for other words if using the contains method
        HashMap<String, Integer> swr = new HashMap<>();

        int wordCounter = 0;
        for (String key : hm.keySet()) {
            boolean foundSwearWord = false;
            for (String swearWord : swearWords) {
                if (key.contains(swearWord)) {
                    if (swr.get(swearWord) != null) {
                        swr.put(swearWord, swr.get(swearWord) + hm.get(key));
                    } else {
                        swr.put(swearWord, hm.get(key));
                    }
                    foundSwearWord = true;
                    wordCounter++;
                    break;
                }
            }
            if (!foundSwearWord && easilyMistakenSwearWords.contains(key)) {
                int existingValue = 0;
                if (swr.get(key) != null) {
                    existingValue = swr.get(key);
                }
                swr.put(key, existingValue + hm.get(key));
            }
            wordCounter++;
        }

        swr = sortByValue(swr, false);

        System.out.println(swr.entrySet());

        System.out.println("Words: " + wordCounter);

        return swr;
    }

    public HashMap<String, Integer> getSwearWordFrequenciesOverTime(List<String> words, int numberOfSections) {
        // numberOfSections 5 by default for now
        System.out.println(words.size());
        int wordsPerSection = words.size() / numberOfSections;
        System.out.println(wordsPerSection);

        List<String> swearWords = Utils.swearWords();
        List<String> easilyMistakenSwearWords = Utils.explicityDefinedSwearWords();

        HashMap<String, Integer> swr = new HashMap<>();

        // starts at 0 as graph will have 0 at start
        for (int i = 0; i <= numberOfSections; i++) {
            swr.put(String.valueOf(i), 0);
        }

        int wordCounter = 0;
        for (String word : words) {
            int currSection = (wordCounter / wordsPerSection) + 1;
            if (currSection > numberOfSections) {
                System.out.println("section before decrement: " + currSection);
                currSection--;
            }
            String section = String.valueOf(currSection);

            boolean foundSwearWord = false;
            for (String swearWord : swearWords) {
                if (word.contains(swearWord)) {
                    int curr = swr.get(section);
                    swr.put(section, curr + 1);
                    System.out.println(format("word: %s  swearword: %s", word, swearWord));
                    foundSwearWord = true;
                    break;
                }
            }
            if (!foundSwearWord && easilyMistakenSwearWords.contains(word)) {
                int curr = swr.get(section);
                swr.put(section, curr + 1);
            }
            wordCounter++;
        }

        System.out.println("wordCounter: " + wordCounter);
//        System.out.println(swr.entrySet());

        return swr;
    }

    // method to sort hashmap by values
    private static HashMap<String, Integer> sortByValue(HashMap<String, Integer> hm, boolean removeSingulars) {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Integer>> list
                = new LinkedList<Map.Entry<String, Integer>>(
                hm.entrySet());

        // Sort the list using lambda expression
        Collections.sort(
                list,
                Comparator.comparing(Map.Entry::getValue));

        // put data from sorted list to hashmap
        HashMap<String, Integer> temp
                = new LinkedHashMap<>();
        for (int i = list.size() - 1; i >= 0; i--) {
            Map.Entry<String, Integer> aa = list.get(i);
            if (removeSingulars) {
                if (aa.getValue() == 1) continue;
            }
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }


}
