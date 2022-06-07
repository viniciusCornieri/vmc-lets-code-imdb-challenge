package vinicius.cornieri.lets.code.challenge.domain.service.mapper;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import vinicius.cornieri.lets.code.challenge.domain.model.Game;
import vinicius.cornieri.lets.code.challenge.domain.model.Movie;
import vinicius.cornieri.lets.code.challenge.domain.model.Round;
import vinicius.cornieri.lets.code.challenge.generated.domain.view.ChoiceDto;
import vinicius.cornieri.lets.code.challenge.generated.domain.view.GameChooseResponseDto;
import vinicius.cornieri.lets.code.challenge.generated.domain.view.MovieDto;
import vinicius.cornieri.lets.code.challenge.generated.domain.view.MovieWithRatingDto;
import vinicius.cornieri.lets.code.challenge.generated.domain.view.RoundDto;
import vinicius.cornieri.lets.code.challenge.generated.domain.view.RoundResultDto;

import java.math.BigDecimal;

class GameChooseResponseDtoMapperTest {

    public static final int FAILURES_COUNT = 2;
    public static final int ROUND_NUMBER = 12;
    public static final int NEXT_ROUND_NUMBER = ROUND_NUMBER + 1;
    public static final String FIRST_MOVIE_IMDB_ID = "tt0001";
    public static final String FIRST_MOVIE_GENRE = "Musical";
    public static final String FIRST_MOVIE_TITLE = "First Movie Title";
    public static final int FIRST_MOVIE_YEAR = 2022;
    public static final String SECOND_MOVIE_IMDB_ID = "tt9999";
    public static final String SECOND_MOVIE_GENRE = "Action";
    public static final String SECOND_MOVIE_TITLE = "Second Movie Title";
    public static final int SECOND_MOVIE_YEAR = 1989;
    public static final String FIRST_MOVIE_RESULT_IMDB_ID = "tt0012";
    public static final String FIRST_MOVIE_RESULT_GENRE = "Documentary";
    public static final String FIRST_MOVIE_RESULT_TITLE = "First Movie Result Title";
    public static final int FIRST_MOVIE_RESULT_YEAR = 1777;
    public static final BigDecimal FIRST_MOVIE_RESULT_RATING = new BigDecimal("7.3");
    public static final BigDecimal FIRST_MOVIE_RESULT_NUM_VOTES = new BigDecimal("1587");
    public static final BigDecimal FIRST_MOVIE_RESULT_SCORE = FIRST_MOVIE_RESULT_RATING.multiply(FIRST_MOVIE_RESULT_NUM_VOTES);
    public static final String SECOND_MOVIE_RESULT_IMDB_ID = "tt1111";
    public static final String SECOND_MOVIE_RESULT_GENRE = "Action";
    public static final String SECOND_MOVIE_RESULT_TITLE = "Second Movie Result Title";
    public static final BigDecimal SECOND_MOVIE_RESULT_RATING = new BigDecimal("2.3");
    public static final BigDecimal SECOND_MOVIE_RESULT_NUM_VOTES = new BigDecimal("200");
    public static final BigDecimal SECOND_MOVIE_RESULT_SCORE = SECOND_MOVIE_RESULT_RATING.multiply(SECOND_MOVIE_RESULT_NUM_VOTES);
    public static final int SECOND_MOVIE_RESULT_YEAR = 2001;
    public static final ChoiceDto CORRECT_ANSWER = ChoiceDto.FIRST;
    public static final boolean GAME_IS_FINISHED = true;
    public static final boolean WAS_ANSWERED_CORRECTLY = false;

    @Test
    void shouldMapGameToGameStartResponse() {
        Game game = getInputGame();

        GameChooseResponseDto response =
            GameChooseResponseDtoMapper.INSTANCE.fromGameAndLastRound(game, getLastRound(), CORRECT_ANSWER);
        RoundDto nextRound = response.getNextRound();
        MovieDto nextRoundFirstMovie = nextRound.getFirstMovieOption();
        MovieDto nexRoundSecondMovie = nextRound.getSecondMovieOption();
        RoundResultDto roundResult = response.getRoundResult();
        MovieWithRatingDto resultFirstMovie = roundResult.getFirstMovieOption();
        MovieWithRatingDto resultSecondMovie = roundResult.getSecondMovieOption();

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(nextRoundFirstMovie).isNotNull();
        soft.assertThat(nexRoundSecondMovie).isNotNull();
        soft.assertThat(resultFirstMovie).isNotNull();
        soft.assertThat(resultSecondMovie).isNotNull();
        soft.assertAll();

        soft.assertThat(response.getFailuresCount()).isEqualTo(FAILURES_COUNT);
        soft.assertThat(response.getFinished()).isTrue();
        soft.assertThat(response.getWasRight()).isEqualTo(WAS_ANSWERED_CORRECTLY);
        soft.assertThat(nextRound.getRoundNumber()).isEqualTo(NEXT_ROUND_NUMBER);

        soft.assertThat(nextRoundFirstMovie.getImdbId()).isEqualTo(FIRST_MOVIE_IMDB_ID);
        soft.assertThat(nextRoundFirstMovie.getGenres()).isEqualTo(FIRST_MOVIE_GENRE);
        soft.assertThat(nextRoundFirstMovie.getTitle()).isEqualTo(FIRST_MOVIE_TITLE);
        soft.assertThat(nextRoundFirstMovie.getYear()).isEqualTo(FIRST_MOVIE_YEAR);

        soft.assertThat(nexRoundSecondMovie.getImdbId()).isEqualTo(SECOND_MOVIE_IMDB_ID);
        soft.assertThat(nexRoundSecondMovie.getGenres()).isEqualTo(SECOND_MOVIE_GENRE);
        soft.assertThat(nexRoundSecondMovie.getTitle()).isEqualTo(SECOND_MOVIE_TITLE);
        soft.assertThat(nexRoundSecondMovie.getYear()).isEqualTo(SECOND_MOVIE_YEAR);

        soft.assertThat(roundResult.getRoundNumber()).isEqualTo(ROUND_NUMBER);
        soft.assertThat(roundResult.getCorrectAnswer()).isEqualTo(CORRECT_ANSWER);
        soft.assertThat(resultFirstMovie.getImdbId()).isEqualTo(FIRST_MOVIE_RESULT_IMDB_ID);
        soft.assertThat(resultFirstMovie.getGenres()).isEqualTo(FIRST_MOVIE_RESULT_GENRE);
        soft.assertThat(resultFirstMovie.getTitle()).isEqualTo(FIRST_MOVIE_RESULT_TITLE);
        soft.assertThat(resultFirstMovie.getYear()).isEqualTo(FIRST_MOVIE_RESULT_YEAR);
        soft.assertThat(resultFirstMovie.getRating()).isEqualTo(FIRST_MOVIE_RESULT_RATING);
        soft.assertThat(resultFirstMovie.getNumVotes()).isEqualTo(FIRST_MOVIE_RESULT_NUM_VOTES.intValue());
        soft.assertThat(resultFirstMovie.getScore()).isEqualTo(FIRST_MOVIE_RESULT_SCORE);


        soft.assertThat(resultSecondMovie.getImdbId()).isEqualTo(SECOND_MOVIE_RESULT_IMDB_ID);
        soft.assertThat(resultSecondMovie.getGenres()).isEqualTo(SECOND_MOVIE_RESULT_GENRE);
        soft.assertThat(resultSecondMovie.getTitle()).isEqualTo(SECOND_MOVIE_RESULT_TITLE);
        soft.assertThat(resultSecondMovie.getYear()).isEqualTo(SECOND_MOVIE_RESULT_YEAR);
        soft.assertThat(resultSecondMovie.getRating()).isEqualTo(SECOND_MOVIE_RESULT_RATING);
        soft.assertThat(resultSecondMovie.getNumVotes()).isEqualTo(SECOND_MOVIE_RESULT_NUM_VOTES.intValue());
        soft.assertThat(resultSecondMovie.getScore()).isEqualTo(SECOND_MOVIE_RESULT_SCORE);
        soft.assertAll();
    }

    private Game getInputGame() {
        Game game = new Game();
        Round round = getNextRound();
        game.setCurrentRound(round);
        game.setFailuresCount(FAILURES_COUNT);
        game.setFinished(GAME_IS_FINISHED);
        return game;
    }

    private Round getLastRound() {
        Round round = new Round();
        round.setRoundNumber(ROUND_NUMBER);
        round.setFirstMovieOption(getResultFirstMovieOption());
        round.setSecondMovieOption(getResultSecondMovieOption());
        round.setWasAnsweredCorrectly(WAS_ANSWERED_CORRECTLY);
        return round;
    }

    private Round getNextRound() {
        Round round = new Round();
        round.setRoundNumber(NEXT_ROUND_NUMBER);
        round.setFirstMovieOption(getFirstMovieOption());
        round.setSecondMovieOption(getSecondMovieOption());
        return round;
    }

    private Movie getSecondMovieOption() {
        Movie secondMovie = new Movie();
        secondMovie.setImdbId(SECOND_MOVIE_IMDB_ID);
        secondMovie.setGenres(SECOND_MOVIE_GENRE);
        secondMovie.setTitle(SECOND_MOVIE_TITLE);
        secondMovie.setYear(SECOND_MOVIE_YEAR);
        return secondMovie;
    }

    private Movie getFirstMovieOption() {
        Movie firstMovie = new Movie();
        firstMovie.setImdbId(FIRST_MOVIE_IMDB_ID);
        firstMovie.setGenres(FIRST_MOVIE_GENRE);
        firstMovie.setTitle(FIRST_MOVIE_TITLE);
        firstMovie.setYear(FIRST_MOVIE_YEAR);
        return firstMovie;
    }

    private Movie getResultSecondMovieOption() {
        Movie secondResultMovie = new Movie();
        secondResultMovie.setImdbId(SECOND_MOVIE_RESULT_IMDB_ID);
        secondResultMovie.setGenres(SECOND_MOVIE_RESULT_GENRE);
        secondResultMovie.setTitle(SECOND_MOVIE_RESULT_TITLE);
        secondResultMovie.setYear(SECOND_MOVIE_RESULT_YEAR);
        secondResultMovie.setNumVotes(SECOND_MOVIE_RESULT_NUM_VOTES);
        secondResultMovie.setRating(SECOND_MOVIE_RESULT_RATING);
        return secondResultMovie;
    }

    private Movie getResultFirstMovieOption() {
        Movie firstResultMovie = new Movie();
        firstResultMovie.setImdbId(FIRST_MOVIE_RESULT_IMDB_ID);
        firstResultMovie.setGenres(FIRST_MOVIE_RESULT_GENRE);
        firstResultMovie.setTitle(FIRST_MOVIE_RESULT_TITLE);
        firstResultMovie.setYear(FIRST_MOVIE_RESULT_YEAR);
        firstResultMovie.setNumVotes(FIRST_MOVIE_RESULT_NUM_VOTES);
        firstResultMovie.setRating(FIRST_MOVIE_RESULT_RATING);
        return firstResultMovie;
    }

}