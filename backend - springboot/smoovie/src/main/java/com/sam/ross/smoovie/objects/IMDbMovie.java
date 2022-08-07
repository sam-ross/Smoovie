package com.sam.ross.smoovie.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IMDbMovie {
    private String id;
    private String resultType;
    private String image;
    private String title;
    private String description;
}
