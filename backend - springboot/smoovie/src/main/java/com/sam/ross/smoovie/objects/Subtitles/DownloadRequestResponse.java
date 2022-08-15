package com.sam.ross.smoovie.objects.subtitles;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DownloadRequestResponse {
    private String link;
    private String file_name;
    private int requests;
    private int remaining;
    private String message;
    private String reset_time;
    private String reset_time_utc;

}
