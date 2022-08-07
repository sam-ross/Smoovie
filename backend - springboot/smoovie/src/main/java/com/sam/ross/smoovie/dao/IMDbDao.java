package com.sam.ross.smoovie.dao;

import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static java.lang.String.format;

@Repository
public class IMDbDao {
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
}
