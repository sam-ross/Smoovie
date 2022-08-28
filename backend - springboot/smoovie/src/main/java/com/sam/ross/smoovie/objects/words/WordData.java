package com.sam.ross.smoovie.objects.words;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WordData {
    private HashMap<String, Integer> wordFrequencies;
    private HashMap<String, Integer> wordFrequenciesWithCommonWords;
    private HashMap<String, Integer> wordLengths;
    private List<PhraseFrequencies> phraseFrequencyRanges;
    private HashMap<String, Integer> swearWordFrequencies;
    private HashMap<String, Integer> swearWordFrequenciesOverTime;
    private int wordCount;
}
