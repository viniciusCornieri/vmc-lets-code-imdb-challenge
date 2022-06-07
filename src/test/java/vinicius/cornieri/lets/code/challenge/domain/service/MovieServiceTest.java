package vinicius.cornieri.lets.code.challenge.domain.service;

import lombok.SneakyThrows;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import vinicius.cornieri.lets.code.challenge.domain.model.Movie;
import vinicius.cornieri.lets.code.challenge.persistence.MovieRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@SpringBootTest
@Sql(scripts = "/sql/tear-down.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class MovieServiceTest {

    public static final int EXPECTED_TOTAL_CATALOG = 279085;
    public static final long RANDOM_MOVIE_ID = 14242L;
    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private MovieService movieService;

    @Test
    @SneakyThrows
    void shouldPopulateMoviesDataAfterApplicationStart() {
        assertThat(movieService.isMovieCatalogEmpty()).isFalse();
        assertThat(movieRepository.count()).isEqualTo(EXPECTED_TOTAL_CATALOG);

        Movie movie = movieRepository.findById(RANDOM_MOVIE_ID)
            .orElseThrow(() -> fail("Not found movie with id " + RANDOM_MOVIE_ID));

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(movie.getImdbId()).isEqualTo("tt0051379");
        softly.assertThat(movie.getTitle()).isEqualTo("Asphalt");
        softly.assertThat(movie.getGenres()).isEqualTo("Drama");
        softly.assertThat(movie.getRating()).isEqualByComparingTo("6.8");
        softly.assertThat(movie.getYear()).isEqualTo(1959);

        softly.assertAll();

    }

    @Test
    void shouldGetRandomMovieReturnADifferentMovieOnConsecutiveCalls() {
        assertThat(movieService.getRandomMovie().getImdbId())
            .isNotEqualTo(movieService.getRandomMovie().getImdbId());

        assertThat(movieService.getRandomMovie().getImdbId())
            .isNotEqualTo(movieService.getRandomMovie().getImdbId());
    }

}