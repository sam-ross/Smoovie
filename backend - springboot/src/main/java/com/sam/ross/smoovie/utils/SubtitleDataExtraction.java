package com.sam.ross.smoovie.utils;

import com.sam.ross.smoovie.exceptions.GeneralException;
import com.sam.ross.smoovie.objects.words.PhraseFrequencies;
import com.sam.ross.smoovie.objects.words.WordData;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SubtitleDataExtraction {
    public static WordData formulateWordData(List<String> words, int numberOfSections) {
        try {
            HashMap<String, Integer> wordFrequencies = getWordFrequencies(words, true);
            HashMap<String, Integer> wordFrequenciesWithCommonWords = getWordFrequencies(words, false);
            HashMap<String, Integer> wordLengths = getWordLengths(words);
            List<PhraseFrequencies> phraseFrequencyRange = getPhraseFrequenciesRange(words);
            HashMap<String, Integer> swearWordFrequencies = getSwearWordFrequencies(words);
            HashMap<String, Integer> swearWordFrequenciesOverTime = getSwearWordFrequenciesOverTime(words,
                    numberOfSections);
            int wordCount = words.size();

            return WordData.builder()
                    .wordFrequencies(wordFrequencies)
                    .wordFrequenciesWithCommonWords(wordFrequenciesWithCommonWords)
                    .wordLengths(wordLengths)
                    .phraseFrequencyRanges(phraseFrequencyRange)
                    .swearWordFrequencies(swearWordFrequencies)
                    .swearWordFrequenciesOverTime(swearWordFrequenciesOverTime)
                    .wordCount(wordCount)
                    .build();
        } catch (Exception e) {
            throw new GeneralException("Error formulating word data: " + e.getMessage());
        }
    }

    public static List<String> parseSubtitleFile(String subtitlesSRT) {
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

        fullSubtitles = fullSubtitles.replaceAll("\\<.*?\\>|\\[.*?\\]|\\{.*?\\}|\\(.*?\\)", "");
        fullSubtitles = fullSubtitles.replace("―", " ").replace("–", " ").replace("-", " ");

        List<String> charactersToRemove = List.of("\"", "“", "”", ",", ".", "…", "♪", "♫", ":", ";", "?", "!", "[",
                "]", "(", ")", "<", ">", "{", "}", "#", "*");
        for (String ch : charactersToRemove) {
            fullSubtitles = fullSubtitles.replace(ch, "");
        }

        fullSubtitles = fullSubtitles.toLowerCase();

        fullSubtitles = fullSubtitles.replace("\n", " ");
        fullSubtitles = fullSubtitles.trim().replaceAll(" +", " "); // reduces multiple spaces to one

        String[] wordsArr = fullSubtitles.split(" ");
        List<String> words = List.of(wordsArr);

        return words;
    }

    private static HashMap<String, Integer> getWordFrequencies(List<String> words, Boolean removeCommonWords) {
        HashMap<String, Integer> hm = new HashMap<>();
        for (String word : words) {
            int value = 1;
            if (hm.containsKey(word)) {
                value += hm.get(word);
            }
            hm.put(word, value);
        }

        if (!removeCommonWords) {
            return sortByValue(hm, true);
        }

        // if the user wants the stopwords removed (default)
        // remove stopwords
        HashMap<String, Integer> hmNoStopWords = hm;
        List<String> stopWords = Utils.getStopWords();

        hmNoStopWords.entrySet().removeIf(k -> stopWords.contains(k.getKey()));

        return sortByValue(hmNoStopWords, true);
    }

    public static HashMap<String, Integer> getWordLengths(List<String> words) {
        HashMap<String, Integer> hm = new HashMap<>();
        for (String word : words) {
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

    private static List<PhraseFrequencies> getPhraseFrequenciesRange(List<String> words) {
        List<PhraseFrequencies> phraseFrequencyRanges = new ArrayList<>();

        for (int i = 2; i <= 8; i++) {
            phraseFrequencyRanges.add(PhraseFrequencies.builder()
                    .phraseFrequencyRange(getPhraseFrequencies(words, i))
                    .build());
        }

        return phraseFrequencyRanges;
    }

    public static HashMap<String, Integer> getPhraseFrequencies(List<String> words, int phraseLength) {
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
        phr = sortByValue(phr, true);

        return phr;
    }

    public static HashMap<String, Integer> getSwearWordFrequencies(List<String> words) {
        // build hashmap
        HashMap<String, Integer> hm = new HashMap<>();
        for (String word : words) {
            int value = 1;
            if (hm.containsKey(word)) {
                value += hm.get(word);
            }
            hm.put(word, value);
        }

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
        if (swr.get("nigg") != null) {
            int curr = swr.get("nigg");
            swr.remove("nigg");
            swr.put("n-word", curr);
        }
        swr = sortByValue(swr, false);

        return swr;
    }

    public static HashMap<String, Integer> getSwearWordFrequenciesOverTime(List<String> words, int numberOfSections) {
        // numberOfSections 5 by default for now
        int wordsPerSection = words.size() / numberOfSections;

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
                currSection--;
            }
            String section = String.valueOf(currSection);

            boolean foundSwearWord = false;
            for (String swearWord : swearWords) {
                if (word.contains(swearWord)) {
                    int curr = swr.get(section);
                    swr.put(section, curr + 1);
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

        // the graph should also finish on zero in the final section - for aesthetics
        swr.put(String.valueOf(numberOfSections + 1), 0);

        return swr;
    }

    // method to sort hashmap by values
    private static HashMap<String, Integer> sortByValue(HashMap<String, Integer> hm, Boolean limitSizeTo60) {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Integer>> list
                = new LinkedList<Map.Entry<String, Integer>>(
                hm.entrySet());

        // Sort the list using lambda expression
        Collections.sort(
                list,
                Comparator.comparing(Map.Entry::getValue));

        // put data from sorted list to hashmap
        int stop = 0;

        if (limitSizeTo60 && list.size() >= 60) {
            stop = list.size() - 60;
        }

        HashMap<String, Integer> temp
                = new LinkedHashMap<>();
        for (int i = list.size() - 1; i >= stop; i--) {
            Map.Entry<String, Integer> aa = list.get(i);
            temp.put(aa.getKey(), aa.getValue());
        }

        return temp;
    }
}
