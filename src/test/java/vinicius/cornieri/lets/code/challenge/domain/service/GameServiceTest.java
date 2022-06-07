package vinicius.cornieri.lets.code.challenge.domain.service;

import io.restassured.RestAssured;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import vinicius.cornieri.lets.code.challenge.domain.model.Game;
import vinicius.cornieri.lets.code.challenge.domain.model.Movie;
import vinicius.cornieri.lets.code.challenge.domain.model.Round;
import vinicius.cornieri.lets.code.challenge.exception.ActiveGameNotFoundException;
import vinicius.cornieri.lets.code.challenge.exception.AlreadyHaveActiveGameException;
import vinicius.cornieri.lets.code.challenge.generated.domain.view.ChoiceDto;
import vinicius.cornieri.lets.code.challenge.generated.domain.view.GameChooseRequestDto;
import vinicius.cornieri.lets.code.challenge.persistence.GameRepository;
import vinicius.cornieri.lets.code.challenge.persistence.MovieRepository;
import vinicius.cornieri.lets.code.challenge.persistence.RoundRepository;

import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static vinicius.cornieri.lets.code.challenge.config.RestResponseExceptionHandler.REQUIRED_REQUEST_BODY_IS_MISSING_MESSAGE;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/tear-down.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class GameServiceTest {

    private static final String GAME_START_ENDPOINT = "/game/start";
    private static final String GAME_CHOOSE_ENDPOINT = "/game/choose";
    private static final long HIGHER_SCORE_MOVIE_ID = 12L;
    private static final long LOWER_SCORE_MOVIE_ID = 15L;
    private static final String GAME_STOP_ENDPOINT = "/game/stop";
    private static final String GAME_CURRENT_ENDPOINT = "/game/current";
    private static final String PLAYER_ONE_API_KEY = "player-one-key";
    private static final String API_KEY = "API-KEY";

    @Autowired
    private RoundRepository roundRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private GameRepository gameRepository;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setup() {
        RestAssured.port = port;
    }

    @Test
    void shouldReturnCreatedWhenStartingNewGame() {
        //@formatter:off
        RestAssured
            .given()
                .header(API_KEY, PLAYER_ONE_API_KEY)
            .when()
                .post(GAME_START_ENDPOINT)
            .then()
                .log().all()
                .statusCode(HttpStatus.CREATED.value())
            .and()
                .body("round.round_number", equalTo(0))
                .body("failures_count", equalTo(0))
                .body("round.firstMovieOption.imdb_id", not(blankOrNullString()))
                .body("round.firstMovieOption.title", not(blankOrNullString()))
                .body("round.secondMovieOption.imdb_id", not(blankOrNullString()))
                .body("round.secondMovieOption.title", not(blankOrNullString()));
        //@formatter:on

    }

    @Test
    void shouldReturnTheCurrentActiveGameAfterCallingGameCurrent() {
        RestAssured
            .given()
                .header(API_KEY, PLAYER_ONE_API_KEY)
                .post(GAME_START_ENDPOINT)
                .then()
            .statusCode(HttpStatus.CREATED.value())
            .body("round.round_number", equalTo(0));


        //@formatter:off
        RestAssured
            .given()
                .header(API_KEY, PLAYER_ONE_API_KEY)
                .get(GAME_CURRENT_ENDPOINT)
            .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
            .and()
                .body("round.round_number", equalTo(0))
                .body("failures_count", equalTo(0))
                .body("round.firstMovieOption.imdb_id", not(blankOrNullString()))
                .body("round.firstMovieOption.title", not(blankOrNullString()))
                .body("round.secondMovieOption.imdb_id", not(blankOrNullString()))
                .body("round.secondMovieOption.title", not(blankOrNullString()));
        //@formatter:on

    }

    @Test
    void shouldReturnBadRequestWhenStartingANewGameAfterOneAlreadyStarted() {
        //@formatter:off
        RestAssured
            .given()
                .header(API_KEY, PLAYER_ONE_API_KEY)
                .post(GAME_START_ENDPOINT)
            .then()
                .statusCode(HttpStatus.CREATED.value());

        RestAssured
            .given()
                .header(API_KEY, PLAYER_ONE_API_KEY)
                .post(GAME_START_ENDPOINT)
            .then()
                .log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
            .and()
                .body("message", equalTo(AlreadyHaveActiveGameException.ERROR_MESSAGE));
        //@formatter:on

    }

    @ParameterizedTest
    @ValueSource(strings = {GAME_CHOOSE_ENDPOINT, GAME_STOP_ENDPOINT})
    void shouldReturnBadRequestWhenCallingChooseOrStopWithoutAGameActive(String endpoint) {
        GameChooseRequestDto input = new GameChooseRequestDto()
            .roundNumber(0)
            .choice(ChoiceDto.FIRST);
        //@formatter:off
        RestAssured
            .given()
                .with()
                .header(API_KEY, PLAYER_ONE_API_KEY)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(input)
                .post(endpoint)
            .then()
            .log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
            .and()
                .body("message", equalTo(ActiveGameNotFoundException.ERROR_MESSAGE));

        RestAssured
            .given()
                .header(API_KEY, PLAYER_ONE_API_KEY)
                .get(GAME_CURRENT_ENDPOINT)
            .then()
                .log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
            .and()
                .body("message", equalTo(ActiveGameNotFoundException.ERROR_MESSAGE));
        //@formatter:on

    }

    @ParameterizedTest
    @ValueSource(strings = {GAME_CHOOSE_ENDPOINT, GAME_STOP_ENDPOINT})
    void shouldReturnBadRequestWhenCallingChooseOrStopAfterGameFinishes(String endpoint) {
        RestAssured
            .given()
                .header(API_KEY, PLAYER_ONE_API_KEY)
                .post(GAME_START_ENDPOINT)
            .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("round.round_number", equalTo(0));

        GameChooseRequestDto input = new GameChooseRequestDto()
            .roundNumber(0)
            .choice(ChoiceDto.FIRST);

        Game game = gameRepository.findAll().get(0);
        game.setFinished(true);
        gameRepository.saveAndFlush(game);

        //@formatter:off
        RestAssured
            .given()
            .with()
                .header(API_KEY, PLAYER_ONE_API_KEY)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(input)
                .post(endpoint)
            .then()
                .log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
            .and()
                .body("message", equalTo(ActiveGameNotFoundException.ERROR_MESSAGE));

        RestAssured
            .given()
                .header(API_KEY, PLAYER_ONE_API_KEY)
                .get(GAME_CURRENT_ENDPOINT)
            .then()
            .log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
            .and()
                .body("message", equalTo(ActiveGameNotFoundException.ERROR_MESSAGE));
        //@formatter:on

    }

    @Test
    void shouldReturnBadRequestWhenCallingChooseWithoutABody() {
        //@formatter:off
        RestAssured
            .given()
            .with()
                .header(API_KEY, PLAYER_ONE_API_KEY)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .post(GAME_CHOOSE_ENDPOINT)
            .then()
                .log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
            .and()
                .body("message", containsString(REQUIRED_REQUEST_BODY_IS_MISSING_MESSAGE));
        //@formatter:on

    }

    @ParameterizedTest
    @CsvSource({
        ",",
        ",FIRST",
        "1,",
    })
    void shouldReturnBadRequestWhenCallingChooseWithoutRequiredParameters(Integer roundNumber, ChoiceDto choice) {
        GameChooseRequestDto input = new GameChooseRequestDto()
            .choice(choice)
            .roundNumber(roundNumber);

        //@formatter:off
        RestAssured
            .given()
            .with()
                .header(API_KEY, PLAYER_ONE_API_KEY)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(input)
                .post(GAME_CHOOSE_ENDPOINT)
            .then()
                .log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
            .and()
                .body("message", containsString("Validation failed"));
        //@formatter:on
    }

    @Test
    void shouldReturnBadRequestWhenRoundNumberItsNotTheCurrentActiveRound() {
        RestAssured
            .given()
                .header(API_KEY, PLAYER_ONE_API_KEY)
                .post(GAME_START_ENDPOINT)
            .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("round.round_number", equalTo(0));

        int invalidRoundNumber = 12;
        GameChooseRequestDto input = new GameChooseRequestDto()
            .choice(ChoiceDto.FIRST)
            .roundNumber(invalidRoundNumber);

        RestAssured
            .given()
            .with()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(API_KEY, PLAYER_ONE_API_KEY)
                .body(input)
                .post(GAME_CHOOSE_ENDPOINT)
            .then()
                .log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
            .and()
                .body("message", equalTo("Invalid round number " + invalidRoundNumber + " current round is 0"));
    }


    @ParameterizedTest
    @CsvSource({
        HIGHER_SCORE_MOVIE_ID + "," + LOWER_SCORE_MOVIE_ID + ",FIRST",
        LOWER_SCORE_MOVIE_ID + "," + HIGHER_SCORE_MOVIE_ID + ",SECOND",
    })
    void shouldReturnOkWhenChooseMovieCorrectly(long firstMovieId, long secondMovieId, String choice) {
        RestAssured
            .given()
                .header(API_KEY, PLAYER_ONE_API_KEY)
                .post(GAME_START_ENDPOINT)
            .then()
                .statusCode(HttpStatus.CREATED.value());

        forceMoviesAtRound(firstMovieId, secondMovieId);

        GameChooseRequestDto input = new GameChooseRequestDto()
            .choice(ChoiceDto.valueOf(choice))
            .roundNumber(0);

        //@formatter:off
        RestAssured
            .given()
            .with()
                .header(API_KEY, PLAYER_ONE_API_KEY)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(input)
                .post(GAME_CHOOSE_ENDPOINT)
            .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
            .and()
            .body("failures_count", equalTo(0))
            .body("wasRight", equalTo(true))
            .body("finished", equalTo(false))
            .body("round_result.correctAnswer", equalTo(choice))
            .body("round_result.round_number", equalTo(0))
            .body("next_round.round_number", equalTo(1));
        //@formatter:on
    }

    @ParameterizedTest
    @CsvSource({
        HIGHER_SCORE_MOVIE_ID + "," + LOWER_SCORE_MOVIE_ID + ",SECOND,FIRST",
        LOWER_SCORE_MOVIE_ID + "," + HIGHER_SCORE_MOVIE_ID + ",FIRST,SECOND",
    })
    void shouldReturnOkWhenChooseWrongMovie(long firstMovieId, long secondMovieId, String choice, String correctAnswer) {
        RestAssured
            .given()
                .header(API_KEY, PLAYER_ONE_API_KEY)
                .post(GAME_START_ENDPOINT)
            .then()
                .statusCode(HttpStatus.CREATED.value());

        forceMoviesAtRound(firstMovieId, secondMovieId);

        GameChooseRequestDto input = new GameChooseRequestDto()
            .choice(ChoiceDto.valueOf(choice))
            .roundNumber(0);

        //@formatter:off
        RestAssured
            .given()
            .with()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(API_KEY, PLAYER_ONE_API_KEY)
                .body(input)
                .post(GAME_CHOOSE_ENDPOINT)
            .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
            .and()
                .body("failures_count", equalTo(1))
                .body("wasRight", equalTo(false))
                .body("finished", equalTo(false))
                .body("round_result.correctAnswer", equalTo(correctAnswer))
                .body("round_result.round_number", equalTo(0))
                .body("next_round.round_number", equalTo(1));
        //@formatter:on
    }

    @Test
    void shouldFinishGameAfterTheThirdFailure() {
        RestAssured
            .given()
                .header(API_KEY, PLAYER_ONE_API_KEY)
                .post(GAME_START_ENDPOINT)
            .then()
                .statusCode(HttpStatus.CREATED.value());

        forceMoviesAtRound(HIGHER_SCORE_MOVIE_ID, LOWER_SCORE_MOVIE_ID);

        Game game = gameRepository.findAll().get(0);
        game.setFailuresCount(2);
        gameRepository.saveAndFlush(game);

        GameChooseRequestDto input = new GameChooseRequestDto()
            .choice(ChoiceDto.SECOND)
            .roundNumber(0);

        //@formatter:off
        RestAssured
            .given()
                .header(API_KEY, PLAYER_ONE_API_KEY)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(input)
                .post(GAME_CHOOSE_ENDPOINT)
            .then()
                .log().all()
            .and()
                .statusCode(HttpStatus.OK.value())
                .body("failures_count", equalTo(3))
                .body("wasRight", equalTo(false))
                .body("finished", equalTo(true))
                .body("round_result.correctAnswer", equalTo("FIRST"))
                .body("round_result.round_number", equalTo(0))
                .body("next_round", nullValue());
        //@formatter:on

        Game updatedGame = gameRepository.findAll().get(0);
        Assertions.assertThat(updatedGame.isFinished()).isTrue();
        Assertions.assertThat(updatedGame.getFinishedAt()).isNotNull();
    }

    @Test
    void shouldFinishGameAfterCallingGameStop() {
        RestAssured
            .given()
            .header(API_KEY, PLAYER_ONE_API_KEY)
            .post(GAME_START_ENDPOINT)
            .then()
            .statusCode(HttpStatus.CREATED.value());

        //@formatter:off
        RestAssured
            .given()
                .header(API_KEY, PLAYER_ONE_API_KEY)
                .post(GAME_STOP_ENDPOINT)
            .then()
                .log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());
        //@formatter:on
        Game updatedGame = gameRepository.findAll().get(0);
        Assertions.assertThat(updatedGame.isFinished()).isTrue();
        Assertions.assertThat(updatedGame.getFinishedAt()).isNotNull();
    }

    private void forceMoviesAtRound(long firstMovieId, long secondMovieId) {
        Round round = roundRepository.findAll().get(0);
        Movie firstMovie = movieRepository.findById(firstMovieId).get();
        Movie secondMovie = movieRepository.findById(secondMovieId).get();
        round.setFirstMovieOption(firstMovie);
        round.setSecondMovieOption(secondMovie);
        roundRepository.saveAndFlush(round);
    }

    @ParameterizedTest
    @ValueSource(strings = {GAME_START_ENDPOINT, GAME_STOP_ENDPOINT})
    void shouldReturnUnauthorizedWhenCallingGameApisWithInvalidApiKey(String endpoint) {

        //@formatter:off
        RestAssured
            .given()
                .post(endpoint)
            .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());

        RestAssured
            .given()
                .header(API_KEY, "INVALID-API-KEY")
                .post(endpoint)
            .then()
            .statusCode(HttpStatus.UNAUTHORIZED.value());
        //@formatter:on
    }

    @Test
    void shouldReturnUnauthorizedWhenCallingGameCurrentWithInvalidApiKey() {
        //@formatter:off
        RestAssured
            .given()
                .get(GAME_CURRENT_ENDPOINT)
            .then()
            .statusCode(HttpStatus.UNAUTHORIZED.value());

        RestAssured
            .given()
            .header(API_KEY, "INVALID-API-KEY")
                .get(GAME_CURRENT_ENDPOINT)
            .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
        //@formatter:on
    }

}