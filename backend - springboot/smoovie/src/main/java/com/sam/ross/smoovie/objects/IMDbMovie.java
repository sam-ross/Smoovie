package com.sam.ross.smoovie.objects;

import lombok.*;

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
