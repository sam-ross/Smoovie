package com.sam.ross.smoovie.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sam.ross.smoovie.dao.IMDbDao;
import com.sam.ross.smoovie.objects.IMDbMovie;
import com.sam.ross.smoovie.objects.IMDbSearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class IMDbService {
    @Autowired
    IMDbDao dao;

    public List<IMDbMovie> searchIMDbMovies(String title, String apiKey) throws IOException, InterruptedException {
        String responseBody = dao.searchIMDbMovies(title, apiKey);
        ObjectMapper mapper = new ObjectMapper();
        IMDbSearchResponse searchResponse = mapper.readValue(responseBody, IMDbSearchResponse.class);

        return searchResponse.getResults();
    }
}
