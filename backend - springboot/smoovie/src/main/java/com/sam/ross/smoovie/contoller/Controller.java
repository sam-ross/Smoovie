package com.sam.ross.smoovie.contoller;

import com.sam.ross.smoovie.objects.IMDbMovie;
import com.sam.ross.smoovie.service.IMDbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
public class Controller {
    @Autowired
    IMDbService service;

    private final String apiKeyImdb = "XXX";

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }

    @GetMapping("/imdb/search/{title}")
    public ResponseEntity<List<IMDbMovie>> searchIMDbMovies(@PathVariable String title) throws IOException,
            InterruptedException {
        List<IMDbMovie> movies = service.searchIMDbMovies(title, apiKeyImdb);

        return ResponseEntity.ok(movies);
    }

//    @PostMapping("/helloPost")
//    public ResponseEntity<String> helloPost(@RequestHeader(value = "name") String name, @RequestHeader(value = "age") String age) {
//        return ResponseEntity.ok("Hello postman " + name + ". You are " + age);
//    }
}
