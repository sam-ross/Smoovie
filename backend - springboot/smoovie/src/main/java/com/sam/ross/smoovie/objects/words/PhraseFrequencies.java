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
public class PhraseFrequencies {
    HashMap<String, Integer> phraseFrequencyRange;
}
