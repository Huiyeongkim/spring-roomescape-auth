package roomescape.integration;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
class ReservationScenarioTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String accessToken;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("INSERT INTO member (login_id, password, name, role) VALUES ('id1', 'pass1', '브라운', 'USER')");
        jdbcTemplate.update("INSERT INTO theme (name, description, url) VALUES ('무서운 이야기', '공포', 'http://example.com')");
        jdbcTemplate.update("INSERT INTO reservation_time (start_at) VALUES ('10:00')");

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
    @DisplayName("예약을 생성하면 내 예약을 조회할 수 있다.")
    void 예약_생성_후_내_예약_조회() {
        Map<String, Object> params = new HashMap<>();
        params.put("date", "2026-08-04");
        params.put("timeId", 1);
        params.put("themeId", 1);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken)
                .body(params)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(200);

        RestAssured.given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .when().get("/reservations/mine")
                .then().log().all()
                .statusCode(200)
                .body("data.size()", is(1));
    }

    @Test
    @DisplayName("예약을 삭제하면 내 예약 목록에서 제거된다.")
    void 예약_생성_후_삭제() {
        Map<String, Object> params = new HashMap<>();
        params.put("date", "2026-08-04");
        params.put("timeId", 1);
        params.put("themeId", 1);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken)
                .body(params)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(200);

        RestAssured.given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .when().delete("/reservations/1")
                .then().log().all()
                .statusCode(200);

        RestAssured.given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .when().get("/reservations/mine")
                .then().log().all()
                .statusCode(200)
                .body("data.size()", is(0));
    }

    @Test
    @DisplayName("예약하면 해당 날짜와 테마의 가용 시간에서 예약된 시간이 제외된다.")
    void 예약_후_해당_시간은_예약_가능_시간에서_제외됨() {
        jdbcTemplate.update("INSERT INTO reservation_time (start_at) VALUES ('11:00')");

        Map<String, Object> params = new HashMap<>();
        params.put("date", "2028-06-04");
        params.put("timeId", 1);
        params.put("themeId", 1);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken)
                .body(params)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(200);

        RestAssured.given().log().all()
                .when().get("/times?themeId=1&date=2028-06-04")
                .then().log().all()
                .statusCode(200)
                .body("data.size()", is(1));
    }
}
