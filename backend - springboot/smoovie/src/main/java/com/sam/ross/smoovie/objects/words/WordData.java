package com.sam.ross.smoovie.objects.words;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WordData {
    private HashMap<String, Integer> wordFrequencies;
    private HashMap<String, Integer> wordFrequenciesWithCommonWords;
    private HashMap<String, Integer> wordLengths;
    private HashMap<String, Integer> phraseFrequencies;
    private HashMap<String, Integer> swearWordFrequencies;
    private HashMap<String, Integer> swearWordFrequenciesOverTime;
}
