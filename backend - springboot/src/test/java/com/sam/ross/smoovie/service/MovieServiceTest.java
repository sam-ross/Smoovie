package com.sam.ross.smoovie.service;

import com.sam.ross.smoovie.dao.MovieDao;
import com.sam.ross.smoovie.objects.IMDbMovie;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {
    @Mock
    MovieDao mockDao;

    @InjectMocks
    MovieService service = new MovieService();

    @Test
    void searchIMDbMoviesShouldReturnAListOfImdbMovies() {
        // given
        IMDbMovie expectedMovie = IMDbMovie.builder()
                .id("tt0407887")
                .resultType("Title")
                .image("https://m.media-amazon.com/images/M/MV5BMTI1MTY2OTIxNV5BMl5BanBnXkFtZTYwNjQ4NjY3._V1_Ratio0" +
                        ".6800_AL_.jpg")
                .title("The Departed")
                .description("(2006)")
                .build();

        when(mockDao.searchIMDbMovies(anyString(), anyString())).thenReturn(buildSearchIMDbMoviesResponse());

        // when
        List<IMDbMovie> response = service.searchIMDbMovies("The departed", "apikey123");

        // then
        verify(mockDao).searchIMDbMovies("The departed", "apikey123");

        IMDbMovie actualMovie;
        if (response.isEmpty()) {
            fail("No movie returned");
        }
        actualMovie = response.get(0);
        assertThat(actualMovie.getId()).isEqualTo(expectedMovie.getId());
        assertThat(actualMovie.getImage()).isEqualTo(expectedMovie.getImage());
    }

//    @Test
//    void getWordDataShouldReturnFormattedWordData() {
//        // given
//        List<Integer> expectedSizes = List.of(20, 33, 10, 7, 20, 5);
//
//        // when
//        WordData response = service.getWordData(buildMovieWordsList(), 3);
//
//        // then
//        List<Integer> actualSizes = List.of(
//                response.getWordFrequencies().size(),
//                response.getWordFrequenciesWithCommonWords().size(),
//                response.getWordLengths().size(),
//                response.getPhraseFrequencyRanges().size(),
//                response.getWordFrequencies().size(),
//                response.getSwearWordFrequenciesOverTime().size()
//        );
//
//        assertThat(actualSizes).isEqualTo(expectedSizes);
//    }

    private String buildSearchIMDbMoviesResponse() {
        return "{\"searchType\":\"Movie\",\"expression\":\"the departed 2006\",\"results\":[{\"id\":\"tt0407887\"," +
                "\"resultType\":\"Title\",\"image\":\"https://m.media-amazon" +
                ".com/images/M/MV5BMTI1MTY2OTIxNV5BMl5BanBnXkFtZTYwNjQ4NjY3._V1_Ratio0.6800_AL_.jpg\",\"title\":\"The" +
                " Departed\",\"description\":\"(2006)\"}],\"errorMessage\":\"\"}";
    }

    private List<String> buildMovieWordsList() {
        return List.of(
                "once",
                "upon",
                "a",
                "time",
                "in",
                "a",
                "kingdom",
                "far",
                "far",
                "away",
                "the",
                "king",
                "and",
                "queen",
                "were",
                "blessed",
                "with",
                "a",
                "beautiful",
                "baby",
                "girl",
                "and",
                "throughout",
                "the",
                "land",
                "everyone",
                "was",
                "happy",
                "until",
                "the",
                "sun",
                "went",
                "down",
                "and",
                "they",
                "saw",
                "that",
                "their",
                "daughter",
                "was",
                "cursed"
        );
    }

}