package vinicius.cornieri.lets.code.challenge.rest.controller;

import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/tear-down.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class GameControllerAuthTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setup() {
        RestAssured.port = port;
    }

    @ParameterizedTest
    @CsvSource({
        "player_one,123",
        "123,player_one"
    })
    void gameStartShouldRejectRequestsWithInvalidCredentials(String user, String pass) {
        RestAssured
            .given()
            .auth()
            .basic(user, pass)
            .when()
            .post("/game")
            .then()
            .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @ParameterizedTest
    @ValueSource(strings = {"player_one", "scd_player"})
    void gameStartShouldAcceptRequestsFromAuthenticatedUsers(String user) {
        RestAssured
            .given()
            .auth()
            .basic(user, user)
            .when()
            .post("/game")
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .body("player", Matchers.equalTo(user));
    }

    @Test
    void gameStartShouldRejectRequestsWithInvalidAuthorities() {
        String visitor = "visitor";
        RestAssured
            .given()
            .auth()
            .basic(visitor, visitor)
            .when()
            .post("/game")
            .then()
            .statusCode(HttpStatus.FORBIDDEN.value());
    }

}