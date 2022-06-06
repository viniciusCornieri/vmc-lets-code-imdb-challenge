package vinicius.cornieri.lets.code.challenge.domain.service;

import io.restassured.RestAssured;
import io.restassured.internal.http.ContentEncoding;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import vinicius.cornieri.lets.code.challenge.exception.ActiveGameNotFoundException;
import vinicius.cornieri.lets.code.challenge.exception.AlreadyHaveActiveGameException;
import vinicius.cornieri.lets.code.challenge.generated.domain.view.ChoiceDto;
import vinicius.cornieri.lets.code.challenge.generated.domain.view.GameChooseRequestDto;

import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static vinicius.cornieri.lets.code.challenge.domain.service.RestResponseExceptionHandler.REQUIRED_REQUEST_BODY_IS_MISSING_MESSAGE;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/tear-down.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class GameServiceIT {

    public static final String GAME_START_ENDPOINT = "/game/start";
    public static final String GAME_CHOOSE_ENDPOINT = "/game/choose";

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
    void shouldReturnBadRequestWhenStartingANewGameAfterOneAlreadyStarted() {
        //@formatter:off
        RestAssured
            .given()
                .post(GAME_START_ENDPOINT)
            .then()
                .statusCode(HttpStatus.CREATED.value());

        RestAssured
            .given()
                .post(GAME_START_ENDPOINT)
            .then()
                .log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
            .and()
                .body("message", equalTo(AlreadyHaveActiveGameException.ERROR_MESSAGE));
        //@formatter:on

    }

    @Test
    void shouldReturnBadRequestWhenCallingChooseWithoutAGameActive() {
        GameChooseRequestDto input = new GameChooseRequestDto()
            .roundNumber(0)
            .choice(ChoiceDto.FIRST);
        //@formatter:off
        RestAssured
            .given()
                .with()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(input)
                .post(GAME_CHOOSE_ENDPOINT)
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

}