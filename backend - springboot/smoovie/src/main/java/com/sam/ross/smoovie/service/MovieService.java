package com.sam.ross.smoovie.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sam.ross.smoovie.dao.MovieDao;
import com.sam.ross.smoovie.objects.IMDbMovie;
import com.sam.ross.smoovie.objects.IMDbSearchResponse;
import com.sam.ross.smoovie.objects.Subtitles.DownloadRequestResponse;
import com.sam.ross.smoovie.objects.Subtitles.SubtilesSearchResponse;
import com.sam.ross.smoovie.objects.Subtitles.Subtitles;
import com.sam.ross.smoovie.objects.WordList;
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

    public WordList getWordList (String imdbId, String apiKey) throws IOException, InterruptedException {
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
        while(matcher.find()) {
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

        List<String> charactersToRemove = List.of("\"", "“", "”", ",", ".", "…", "♪", ":", ";", "?", "!", "[", "]", "(", ")", "<", ">", "{", "}");
        for (String ch: charactersToRemove) {
            fullSubtitles = fullSubtitles.replace(ch, "");
        }

        fullSubtitles = fullSubtitles.toLowerCase();

        fullSubtitles = fullSubtitles.replace("\n", " ");
        fullSubtitles = fullSubtitles.trim().replaceAll(" +", " "); // reduces multiple spaces to one

//        fullSubtitles = fullSubtitles
//                .toLowerCase()
//                .replace("\n", " ")
//                .trim()
//                .replaceAll(" +", " "); // reduces multiple spaces to one

        String[] wordsArr = fullSubtitles.split(" ");
        List<String> words = List.of(wordsArr);

        for (String word: words) {
            System.out.println(format("\"%s\"", word));
        }

        return words;
    }

    public HashMap<String, Integer> getWordFrequencies(List<String> words) {
        HashMap<String, Integer> hm = new HashMap<>();
        for (String word: words) {
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

        return hm;
    }

    // method to sort hashmap by values
    private static HashMap<String, Integer> sortByValue(HashMap<String, Integer> hm, boolean removeSingulars)
    {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Integer> > list
                = new LinkedList<Map.Entry<String, Integer> >(
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
