package vinicius.cornieri.lets.code.challenge.domain.service;

import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GameServiceTest {

    public static final String GAME_START_ENDPOINT = "/game/start";
    @LocalServerPort
    private int port;

    @BeforeEach
    void testSetup() {
        RestAssured.port = port;
    }

    @Test
    void shouldReturnOkWhenStartingNewGame() {
        //@formatter:off
        RestAssured
            .given()
            .when()
                .post(GAME_START_ENDPOINT)
            .then()
                .log().all()
                .statusCode(200)
            .and()
                .body("round_number", equalTo(0))
                .body("failures_count", equalTo(0))
                .body("firstMovieOption.imdb_id", not(blankOrNullString()))
                .body("firstMovieOption.title", not(blankOrNullString()))
                .body("secondMovieOption.imdb_id", not(blankOrNullString()))
                .body("secondMovieOption.title", not(blankOrNullString()));
        //@formatter:on

    }

    @Test
    void shouldReturnBadRequestWhenStartingANewGameAfterOneAlreadyStarted() {
        //@formatter:off
        RestAssured
            .given()
                .post(GAME_START_ENDPOINT)
            .then()
                .statusCode(200);

        RestAssured
            .given()
                .post(GAME_START_ENDPOINT)
            .then()
                .log().all()
                .statusCode(400)
            .and()
                .body("message", equalTo("Player have already an active game started, continue playing at /game/current and /game/choose endpoints"));
        //@formatter:on

    }

}