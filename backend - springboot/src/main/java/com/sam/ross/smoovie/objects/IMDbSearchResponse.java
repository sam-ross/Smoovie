package com.sam.ross.smoovie.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IMDbSearchResponse {
    private String searchType;
    private String expression;
    private List<IMDbMovie> results;
    private String errorMessage;
}
