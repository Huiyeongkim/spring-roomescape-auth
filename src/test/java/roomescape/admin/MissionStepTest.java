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
class MissionStepTest {

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
    void 예약_조회() {
        RestAssured.given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("data.size()", is(0));
    }

    @Test
    void 예약_추가_및_삭제() {
        jdbcTemplate.update("INSERT INTO theme (name, description, url) VALUES (?, ?, ?)", "무서워", "akdk", "https://hello.com");
        jdbcTemplate.update("INSERT INTO reservation_time (start_at) VALUES (?)", "15:40");

        Map<String, Object> params = new HashMap<>();
        params.put("date", "2026-08-05");
        params.put("timeId", 1);
        params.put("themeId", 1);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken)
                .body(params)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("data.id", is(1));

        RestAssured.given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("data.size()", is(1));

        RestAssured.given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .when().delete("/reservations/1")
                .then().log().all()
                .statusCode(200);

        RestAssured.given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("data.size()", is(0));
    }
}
