package vinicius.cornieri.lets.code.challenge.domain.service;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/tear-down.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class PlayerServiceTest {

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
}