package com.sam.ross.smoovie.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WordFrequency {
    private HashMap<String, Integer> wordFrequencies;
}
