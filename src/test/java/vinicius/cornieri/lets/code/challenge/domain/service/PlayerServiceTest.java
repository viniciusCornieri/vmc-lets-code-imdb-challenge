package vinicius.cornieri.lets.code.challenge.domain.service;

import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import vinicius.cornieri.lets.code.challenge.generated.domain.view.PlayerDto;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/tear-down.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class PlayerServiceTest {

    public static final String STRING_WITH_101_CHARS =
        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab";
    @LocalServerPort
    private int port;

    @BeforeEach
    void setup() {
        RestAssured.port = port;
    }

    @Test
    void shouldReturnOkThePlayerLeaderboard() {
        //@formatter:off
        RestAssured
            .given()
            .when()
                .get("/player/leaderboard")
            .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
            .and()
                // magic numbers from insert-initial-players.yaml
                .body("[0].nickname", equalTo("Ready Player One"))
                .body("[0].score", equalTo(9999))
                .body("[1].nickname", equalTo("Green Mario"))
                .body("[1].score", equalTo(1991));
        //@formatter:on
    }

    @Test
    void shouldReturnCreatedWhenCreatingANewPlayer() {
        String someNickname = "some nickname";
        PlayerDto input = new PlayerDto().nickname(someNickname);

        //@formatter:off
        RestAssured
            .given()
                .when()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(input)
                .post("/player")
            .then()
                .log().all()
                .statusCode(HttpStatus.CREATED.value())
            .and()
                .body("nickname", equalTo(someNickname))
                .body("api-key", Matchers.not(Matchers.blankOrNullString()));
        //@formatter:on
    }

    @Test
    void shouldReturnBadRequestWhenCreatingAPlayerWithDuplicatedNickname() {
        String playerOneNickname = "Ready Player One";
        PlayerDto input = new PlayerDto().nickname(playerOneNickname);

        //@formatter:off
        RestAssured
            .given()
            .when()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(input)
                .post("/player")
            .then()
                .log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
            .and()
                .body("message", equalTo("Nickname " + playerOneNickname + " already exists"));
        //@formatter:on
    }

    @ParameterizedTest
    @ValueSource(strings = {"a", "ab", STRING_WITH_101_CHARS})
    @NullAndEmptySource
    void shouldReturnBadRequestWhenCreatingAPlayerWithInvalidNickname(String nickname) {
        PlayerDto input = new PlayerDto().nickname(nickname);

        //@formatter:off
        RestAssured
            .given()
            .when()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(input)
                .post("/player")
            .then()
                .log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
            .and()
                .body("message", containsString("Validation failed:"))
                .body("fields[0].field", equalTo("nickname"));
        //@formatter:on
    }
}