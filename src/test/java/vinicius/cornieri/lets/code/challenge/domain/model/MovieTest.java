package vinicius.cornieri.lets.code.challenge.domain.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MovieTest {

    @Test
    void getScoreShouldReturnTheMultipeOfRatingAndNumVotes() {
        Movie movie = new Movie();
        movie.setRating(new BigDecimal("6.4"));
        movie.setNumVotes(new BigDecimal("1000"));

        Assertions.assertThat(movie.getScore()).isEqualTo("6400");

        movie.setRating(new BigDecimal("7.2"));
        movie.setNumVotes(new BigDecimal("755"));

        Assertions.assertThat(movie.getScore()).isEqualTo("5436");
    }

}