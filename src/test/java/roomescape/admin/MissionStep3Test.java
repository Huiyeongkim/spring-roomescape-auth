package roomescape.admin;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class MissionStep3Test {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String accessToken;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("INSERT INTO member (login_id, password, name, role) VALUES ('id1', 'pass1', '브라운', 'USER')");

        Map<String, String> loginParams = new HashMap<>();
        loginParams.put("loginId", "id1");
        loginParams.put("password", "pass1");

        accessToken = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(loginParams)
                .when().post("/login")
                .then()
                .statusCode(200)
                .extract().jsonPath().getString("data.accessToken");
    }

    @Test
    void 시간_관리_API() {
        Map<String, String> params = new HashMap<>();
        params.put("startAt", "10:00");

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken)
                .body(params)
                .when().post("/admin/times")
                .then().log().all()
                .statusCode(200);

        RestAssured.given().log().all()
                .when().get("/times")
                .then().log().all()
                .statusCode(200)
                .body("data.size()", is(1));

        RestAssured.given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .when().delete("/admin/times/1")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    void 예약과_시간_연결() {
        Map<String, String> themeParams = new HashMap<>();
        themeParams.put("name", "무서워");
        themeParams.put("description", "abc");
        themeParams.put("url", "https://hello.com");

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken)
                .body(themeParams)
                .when().post("/admin/themes")
                .then().log().all()
                .statusCode(200);

        Map<String, String> timeParams = new HashMap<>();
        timeParams.put("startAt", "10:00");

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken)
                .body(timeParams)
                .when().post("/admin/times")
                .then().log().all()
                .statusCode(200);

        Map<String, Object> reservation = new HashMap<>();
        reservation.put("date", "2026-08-05");
        reservation.put("timeId", 1);
        reservation.put("themeId", 1);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken)
                .body(reservation)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(200);

        RestAssured.given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("data.size()", is(1));

        Map<String, Object> updatedReservation = new HashMap<>();
        updatedReservation.put("date", "2026-08-06");
        updatedReservation.put("timeId", 1);
        updatedReservation.put("themeId", 1);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken)
                .body(updatedReservation)
                .when().put("/reservations/1")
                .then().log().all()
                .statusCode(200);
    }
}
