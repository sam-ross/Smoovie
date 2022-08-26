package com.sam.ross.smoovie.contoller;

import com.sam.ross.smoovie.exceptions.ServiceProxyException;
import com.sam.ross.smoovie.objects.IMDbMovie;
import com.sam.ross.smoovie.objects.IMDbMovieList;
import com.sam.ross.smoovie.objects.words.WordData;
import com.sam.ross.smoovie.objects.words.WordList;
import com.sam.ross.smoovie.service.MovieService;
import com.sam.ross.smoovie.utils.SubtitleDataExtraction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
public class MovieController {
    @Autowired
    private MovieService service;

    private final String apiKeyImdb = "";
    private final String apiKeyOpenSubtitles = "";
    private final String username = "";
    private final String password = "";

    @CrossOrigin
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("{\"ping\": \"pong\"}");
    }

    @CrossOrigin
    @GetMapping("/imdb/search/{title}")
    public ResponseEntity<IMDbMovieList> searchIMDbMovies(@PathVariable String title) {
        List<IMDbMovie> movies = service.searchIMDbMovies(title, apiKeyImdb);
        if (movies.size() > 5) {
            movies = movies.subList(0, 5);
        }

        return ResponseEntity.ok(IMDbMovieList.builder().movies(movies).build());
    }

    @CrossOrigin
    @GetMapping("/words/{imdbId}")
    public ResponseEntity<WordList> getWordList(@PathVariable String imdbId) {
        WordList words = service.getWordList(imdbId, apiKeyOpenSubtitles, username, password);

        return ResponseEntity.ok(words);
    }

    @CrossOrigin
    @PostMapping("/words/data")
    public ResponseEntity<WordData> getWordData(
            @RequestBody List<String> words,
            @RequestParam(defaultValue = "19") int numberOfSections
    ) {
        if (words.isEmpty()) {
            throw ServiceProxyException.builder()
                    .httpStatus(506)
                    .message("Unexpected error returned")
                    .build();
        }

        WordData wordData = service.getWordData(words, numberOfSections);

        return ResponseEntity.ok(wordData);
    }

    //
    @PostMapping("/words/lengths")
    public ResponseEntity<HashMap<String, Integer>> getWordLengths(@RequestBody List<String> words) {
        HashMap<String, Integer> wordLengths = SubtitleDataExtraction.getWordLengths(words);

        System.out.println(wordLengths);

        return ResponseEntity.ok(wordLengths);
    }

    //
    @PostMapping("/words/phrases")
    public ResponseEntity<HashMap<String, Integer>> getPhrases(
            @RequestBody List<String> words,
            @RequestParam(defaultValue = "3") int phraseLength
    ) {
        HashMap<String, Integer> phrases = SubtitleDataExtraction.getPhraseFrequencies(words, phraseLength);

        System.out.println(phrases);

        return ResponseEntity.ok(phrases);
    }

    //
    @PostMapping("/words/swear")
    public ResponseEntity<HashMap<String, Integer>> getSwearWords(@RequestBody List<String> words) {
        HashMap<String, Integer> swearWords = SubtitleDataExtraction.getSwearWordFrequencies(words);

        System.out.println(swearWords);

        return ResponseEntity.ok(swearWords);
    }

    //
    @PostMapping("/words/swear/time")
    public ResponseEntity<HashMap<String, Integer>> getSwearWordsOverTime(
            @RequestBody List<String> words,
            @RequestParam(defaultValue = "5") int numberOfSections
    ) {
        HashMap<String, Integer> swearWords = SubtitleDataExtraction.getSwearWordFrequenciesOverTime(
                words,
                numberOfSections
        );

        System.out.println(swearWords);

        return ResponseEntity.ok(swearWords);
    }

}
