package com.sam.ross.smoovie.objects.Subtitles;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subtitles {
    private String subtitlesSRT;
    private List<String> words;
}
