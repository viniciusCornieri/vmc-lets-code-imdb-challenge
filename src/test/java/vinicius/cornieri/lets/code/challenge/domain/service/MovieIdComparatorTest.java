package vinicius.cornieri.lets.code.challenge.domain.service;

import org.junit.jupiter.api.Test;
import vinicius.cornieri.lets.code.challenge.domain.model.Movie;

import static org.assertj.core.api.Assertions.assertThat;

class MovieIdComparatorTest {

    private static final long LOWER_ID = 1L;
    private static final long HIGHER_ID = 258L;

    @Test
    void lowerIdMovieShouldReturnTheMovieWithLowerId() {
        Movie lowerIdMovie = getLowerIdMovie();
        Movie higherIdMovie = getHigherIdMovie();

        assertThat(MovieIdComparator.lowerIdMovie(lowerIdMovie, higherIdMovie)).isEqualTo(lowerIdMovie);
        assertThat(MovieIdComparator.lowerIdMovie(higherIdMovie, lowerIdMovie)).isEqualTo(lowerIdMovie);
    }

    @Test
    void higherIdMovieShouldReturnTheMovieWithHigherId() {
        Movie lowerIdMovie = getLowerIdMovie();
        Movie higherIdMovie = getHigherIdMovie();

        assertThat(MovieIdComparator.lowerIdMovie(lowerIdMovie, higherIdMovie)).isEqualTo(higherIdMovie);
        assertThat(MovieIdComparator.lowerIdMovie(higherIdMovie, lowerIdMovie)).isEqualTo(higherIdMovie);
    }

    private Movie getHigherIdMovie() {
        Movie higherIdMovie = new Movie();
        higherIdMovie.setId(HIGHER_ID);
        return higherIdMovie;
    }

    private Movie getLowerIdMovie() {
        Movie lowerIdMovie = new Movie();
        lowerIdMovie.setId(LOWER_ID);
        return lowerIdMovie;
    }

}