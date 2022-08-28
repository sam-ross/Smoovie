package com.sam.ross.smoovie.contoller;

import com.sam.ross.smoovie.objects.IMDbMovie;
import com.sam.ross.smoovie.objects.IMDbMovieList;
import com.sam.ross.smoovie.objects.words.WordData;
import com.sam.ross.smoovie.service.MovieService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = {"https://smoovie.app/", "https://smoovie-react.nw.r.appspot.com/"})
@Slf4j
public class MovieController {
    @Autowired
    private MovieService service;

    @Value("${imdb.api.key}")
    private String apiKeyImdb;
    @Value("${opensubtitles.api.key}")
    private String apiKeyOpenSubtitles;
    @Value("${opensubtitles.username}")
    private String username;
    @Value("${opensubtitles.password}")
    private String password;

    @GetMapping("/imdb/search/{title}")
    public ResponseEntity<IMDbMovieList> searchIMDbMovies(@PathVariable String title) {
        log.info("searchIMDbMovies endpoint has received a request (controller)");
        List<IMDbMovie> movies = service.searchIMDbMovies(title, apiKeyImdb);
        if (movies.size() > 5) {
            movies = movies.subList(0, 5);
        }

        return ResponseEntity.ok(IMDbMovieList.builder().movies(movies).build());
    }

    @GetMapping("/words/data/{imdbId}")
    public ResponseEntity<WordData> getWordData(
            @PathVariable String imdbId, @RequestParam(defaultValue = "19") int numberOfSections
    ) {
        log.info("getWordData endpoint has received a request (controller)");
        WordData wordData = service.getWordData(imdbId, apiKeyOpenSubtitles, username, password, numberOfSections);

        return ResponseEntity.ok(wordData);
    }

    @CrossOrigin
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        log.info("ping endpoint has received a request (controller)");

        return ResponseEntity.ok("pong");
    }

}
