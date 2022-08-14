package com.sam.ross.smoovie.dao;

import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static java.lang.String.format;

@Repository
public class MovieDao {
    private static final String OPEN_SUBTITLES_BASE_URL = "https://api.opensubtitles.com/api/v1";

    public String searchIMDbMovies(String title, String apiKey) throws IOException, InterruptedException {
        String convertedMovieName = title.replace(" ", "%20");
        HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(format("https://imdb-api.com/en/API/SearchMovie/%s/%s", apiKey, convertedMovieName)))
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() != 200) {
            System.out.println(format("Unexpected status code: %d", response.statusCode()));
        }

        return response.body();
    }

    public String searchForSubtitles(String imdbId, String apiKey) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
        HttpRequest request = HttpRequest.newBuilder()
                .header("Api-Key", apiKey)
                .uri(URI.create(OPEN_SUBTITLES_BASE_URL + "/subtitles?languages=en&imdb_id=" + imdbId))
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() != 200) {
            System.out.println(format("Unexpected status code: %d", response.statusCode()));
        }

        return response.body();
    }

    public String requestForDownload(String fileId, String apiKey) throws IOException, InterruptedException {
        String requestBody = format("{\"file_id\": %s}", fileId);
        HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(OPEN_SUBTITLES_BASE_URL + "/download"))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header("Api-Key", apiKey)
                .header("Content-type", "application/json")
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() != 200) {
            System.out.println(format("Unexpected status code: %d", response.statusCode()));
        }

        System.out.println(response.body());
        return response.body();
    }

    public String useDownloadLink(String downloadLink) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(downloadLink))
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() != 200) {
            System.out.println(format("Unexpected status code: %d", response.statusCode()));
        }

        return response.body();
    }
}
