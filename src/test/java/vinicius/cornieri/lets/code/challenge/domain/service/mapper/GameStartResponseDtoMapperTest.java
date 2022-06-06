package vinicius.cornieri.lets.code.challenge.domain.service.mapper;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import vinicius.cornieri.lets.code.challenge.domain.model.Game;
import vinicius.cornieri.lets.code.challenge.domain.model.Movie;
import vinicius.cornieri.lets.code.challenge.domain.model.Round;
import vinicius.cornieri.lets.code.challenge.generated.domain.view.GameStartResponseDto;
import vinicius.cornieri.lets.code.challenge.generated.domain.view.MovieDto;
import vinicius.cornieri.lets.code.challenge.generated.domain.view.RoundDto;

class GameStartResponseDtoMapperTest {

    public static final int FAILURES_COUNT = 2;
    public static final int ROUND_NUMBER = 12;
    public static final String FIRST_MOVIE_IMDB_ID = "tt0001";
    public static final String FIRST_MOVIE_GENRE = "Musical";
    public static final String FIRST_MOVIE_TITLE = "First Movie Title";
    public static final int FIRST_MOVIE_YEAR = 2022;
    public static final String SECOND_MOVIE_IMDB_ID = "tt9999";
    public static final String SECOND_MOVIE_GENRE = "Action";
    public static final String SECOND_MOVIE_TITLE = "Second Movie Title";
    public static final int SECOND_MOVIE_YEAR = 1989;

    @Test
    void shouldMapGameToGameStartResponse() {
        Game game = getInputGame();

        GameStartResponseDto response = GameStartResponseDtoMapper.INSTANCE.fromGame(game);
        RoundDto round = response.getRound();
        MovieDto firstMovieOption = round.getFirstMovieOption();
        MovieDto secondMovieOption = round.getSecondMovieOption();

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(firstMovieOption).isNotNull();
        soft.assertThat(secondMovieOption).isNotNull();
        soft.assertAll();

        soft.assertThat(response.getFailuresCount()).isEqualTo(FAILURES_COUNT);
        soft.assertThat(round.getRoundNumber()).isEqualTo(ROUND_NUMBER);
        soft.assertThat(firstMovieOption.getImdbId()).isEqualTo(FIRST_MOVIE_IMDB_ID);
        soft.assertThat(firstMovieOption.getGenres()).isEqualTo(FIRST_MOVIE_GENRE);
        soft.assertThat(firstMovieOption.getTitle()).isEqualTo(FIRST_MOVIE_TITLE);
        soft.assertThat(firstMovieOption.getYear()).isEqualTo(FIRST_MOVIE_YEAR);

        soft.assertThat(secondMovieOption.getImdbId()).isEqualTo(SECOND_MOVIE_IMDB_ID);
        soft.assertThat(secondMovieOption.getGenres()).isEqualTo(SECOND_MOVIE_GENRE);
        soft.assertThat(secondMovieOption.getTitle()).isEqualTo(SECOND_MOVIE_TITLE);
        soft.assertThat(secondMovieOption.getYear()).isEqualTo(SECOND_MOVIE_YEAR);
        soft.assertAll();
    }

    private Game getInputGame() {
        Game game = new Game();
        Round round = getInputRound();
        game.setCurrentRound(round);
        game.setFailuresCount(FAILURES_COUNT);
        return game;
    }

    private Round getInputRound() {
        Round round = new Round();
        round.setRoundNumber(ROUND_NUMBER);
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

}