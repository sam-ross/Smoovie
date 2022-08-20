package com.sam.ross.smoovie.dao;

import com.sam.ross.smoovie.exceptions.ServiceProxyException;
import org.springframework.stereotype.Repository;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static java.lang.String.format;

@Repository
public class MovieDao {
    private static final String OPEN_SUBTITLES_BASE_URL = "https://api.opensubtitles.com/api/v1";

    /**
     * Calls the "search movie" endpoint in the IMDb API to retrieve the 5 most relevant matches to the title
     *
     * @param title  - Title of the movie we're trying to find
     * @param apiKey - IMDb API key
     * @return JSON response string to be used in the service to extract the IMDb ID
     */
    public String searchIMDbMovies(String title, String apiKey) {
        String convertedMovieName = title.replace(" ", "%20");
        HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(format("https://imdb-api.com/en/API/SearchMovie/%s/%s", apiKey, convertedMovieName)))
                .build();

        HttpResponse<String> response = getHttpResponse(client, request);

        return response.body();
    }

    /**
     * Calls the "search for subtitles" endpoint in the OpenSubtitles API to retrieve a list of different subtitle
     * files they have for that movie
     *
     * @param imdbId - The IMDb ID is used to specify which movie we are requesting subtitles for
     * @param apiKey - OpenSubtitles API key
     * @return JSON response string to be used in the service to extract the unique OpenSubtitles file ID for
     * that particular subtitle file
     */
    public String searchForSubtitles(String imdbId, String apiKey) {
        HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
        HttpRequest request = HttpRequest.newBuilder()
                .header("Api-Key", apiKey)
                .uri(URI.create(OPEN_SUBTITLES_BASE_URL + "/subtitles?languages=en&imdb_id=" + imdbId))
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = getHttpResponse(client, request);

        return response.body();
    }

    /**
     * Calls the "download subtitles" endpoint in the OpenSubtitles API to retrieve a download link which will
     * be used later to download the subtitles SRT file
     *
     * @param fileId - The unique OpenSubtitles file ID to specify which subtitle file we are requesting the
     *               download link for
     * @param apiKey - OpenSubtitles API key
     * @return JSON response string to be used in the service to extract the actual download link which will be
     * used later to download the subtitles file
     */
    public String requestForDownload(String fileId, String apiKey) {
        String requestBody = format("{\"file_id\": %s}", fileId);
        HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(OPEN_SUBTITLES_BASE_URL + "/download"))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header("Api-Key", apiKey)
                .header("Content-type", "application/json")
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = getHttpResponse(client, request);

        System.out.println(response.body());

        return response.body();
    }

    /**
     * Calls the actual download link for the subtitles SRT file which will be used later to extract all the words
     *
     * @param downloadLink - The download link of the SRT file we are trying to download from OpenSubtitles
     * @return JSON response string containing the SRT file contents of the OpenSubtitles file
     */
    public String useDownloadLink(String downloadLink) {
        HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(downloadLink))
                .build();

        HttpResponse<String> response = getHttpResponse(client, request);

        return response.body();
    }

    /**
     * Executes the HTTP request out to the respective client, whether that's the IMDb API or the OpenSubtitles API
     *
     * @param client  - The client we are making the HTTP request out to
     * @param request - The request that we are sending to the HTTP client
     * @return The HTTP response returned from the respective client
     */
    private HttpResponse<String> getHttpResponse(HttpClient client, HttpRequest request) {
        HttpResponse<String> response;
        try {
            response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );
        } catch (Exception e) {
            throw ServiceProxyException.builder()
                    .httpStatus(502)
                    .message("Http request to client failed")
                    .build();
        }

        if (response.statusCode() != 200) {
            System.out.println(format("Unexpected status code: %d", response.statusCode()));
            throw ServiceProxyException.builder()
                    .httpStatus(response.statusCode())
                    .message(response.body())
                    .build();
        }

        return response;
    }
}
