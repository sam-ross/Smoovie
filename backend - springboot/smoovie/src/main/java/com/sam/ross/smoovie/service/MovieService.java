package com.sam.ross.smoovie.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sam.ross.smoovie.dao.MovieDao;
import com.sam.ross.smoovie.exceptions.EmptyResponseException;
import com.sam.ross.smoovie.exceptions.GeneralException;
import com.sam.ross.smoovie.objects.IMDbMovie;
import com.sam.ross.smoovie.objects.IMDbSearchResponse;
import com.sam.ross.smoovie.objects.subtitles.DownloadRequestResponse;
import com.sam.ross.smoovie.objects.subtitles.SubtitlesSearchResponse;
import com.sam.ross.smoovie.objects.words.WordData;
import com.sam.ross.smoovie.objects.words.WordList;
import com.sam.ross.smoovie.utils.SubtitleDataExtraction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieService {
    @Autowired
    private MovieDao dao;

    public List<IMDbMovie> searchIMDbMovies(String title, String apiKey) {
        String responseBody = dao.searchIMDbMovies(title, apiKey);
        ObjectMapper mapper = new ObjectMapper();
        IMDbSearchResponse searchResponse;

        try {
            searchResponse = mapper.readValue(responseBody, IMDbSearchResponse.class);
        } catch (JsonProcessingException e) {
            throw new GeneralException("Error parsing JSON Object into IMDbSearchResponse class");
        }

        if (searchResponse.getResults().isEmpty()) {
            throw new EmptyResponseException("Empty response returned from the IMDb API");
        }

        return searchResponse.getResults();
    }

    public WordList getWordList(String imdbId, String apiKey) {
        if (imdbId.length() > 1 && imdbId.charAt(0) == 't' && imdbId.charAt(1) == 't') {
            imdbId = imdbId.substring(2);
        }
        System.out.println(imdbId);

        // get file id
        String fileId = getFileId(imdbId, apiKey);
        System.out.println(fileId);

        // get download link
        String downloadLink = getDownloadLink(fileId, apiKey);
        System.out.println(downloadLink);

        // use download link
        String subtitlesSRT = downloadSubtitles(downloadLink);

        // parse subtitles
        List<String> words = SubtitleDataExtraction.parseSubtitleFile(subtitlesSRT);

        // return subtitles
        return WordList.builder()
                .words(words)
                .build();
    }

    private String getFileId(String imdbId, String apiKey) {
        String responseBody = dao.searchForSubtitles(imdbId, apiKey);
        ObjectMapper mapper = new ObjectMapper();
        SubtitlesSearchResponse subtitlesSearchResponse;

        try {
            subtitlesSearchResponse = mapper.readValue(responseBody, SubtitlesSearchResponse.class);
        } catch (JsonProcessingException e) {
            throw new GeneralException("Error parsing JSON Object into SubtitlesSearchResponse class");
        }

        if (subtitlesSearchResponse.getTotal_count().equals("0")) {
            throw new EmptyResponseException("Empty response returned from the OpenSubtitles API");
        }

        try {
            return subtitlesSearchResponse.getData()
                    .get(0)
                    .get("attributes")
                    .get("files")
                    .get(0)
                    .get("file_id")
                    .asText();
        } catch (Exception e) {
            throw new GeneralException("Subtitles JSON response returned in unexpected format: " + e.getMessage());
        }

    }

    private String getDownloadLink(String fileId, String apiKey) {
        String responseBody = dao.requestForDownload(fileId, apiKey);
        ObjectMapper mapper = new ObjectMapper();
        DownloadRequestResponse downloadRequestResponse;

        try {
            downloadRequestResponse = mapper.readValue(responseBody, DownloadRequestResponse.class);
        } catch (JsonProcessingException e) {
            throw new GeneralException("Error parsing JSON Object into DownloadRequestResponse class");
        }

        String downloadLink = downloadRequestResponse.getLink();
        if (downloadLink.length() < 5 || !downloadLink.substring(0, 4).equals("http")) {
            throw new GeneralException("Unexpected download link returned: " + downloadLink);
        }

        return downloadLink;
    }

    private String downloadSubtitles(String downloadLink) {
        String responseBody = dao.useDownloadLink(downloadLink);
        System.out.println(responseBody);

        if (responseBody.length() == 0) {
            throw new GeneralException("Empty SRT file downloaded using the following download link: " + downloadLink);
        }

        return responseBody;
    }

    public WordData getWordData(List<String> words, int numberOfSections) {
        return SubtitleDataExtraction.formulateWordData(words, numberOfSections);
    }

}
