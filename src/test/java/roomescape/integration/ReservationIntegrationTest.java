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
class ReservationIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String accessToken;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("INSERT INTO member (login_id, password, name, role) VALUES ('id1', 'pass1', '브라운', 'USER')");
        jdbcTemplate.update("INSERT INTO theme (name, description, url) VALUES ('무서운 이야기', '공포', 'http://example.com')");
        jdbcTemplate.update("INSERT INTO reservation_time (start_at) VALUES ('10:00')");
        jdbcTemplate.update("INSERT INTO reservation_time (start_at) VALUES ('11:00')");
        jdbcTemplate.update("INSERT INTO store (name, member_id) VALUES ('브라운 매장', 1)");

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
    @DisplayName("예약을 생성할 수 있다.")
    void 예약_생성_성공() {
        Map<String, Object> params = new HashMap<>();
        params.put("date", "2026-08-04");
        params.put("timeId", 1);
        params.put("themeId", 1);
        params.put("storeId", 1);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken)
                .body(params)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("data.date", is("2026-08-04"));

    }

    @Test
    @DisplayName("예약이 없으면 전체 목록 조회 시 빈 결과를 반환한다.")
    void 예약_없을때_전체_목록_조회() {
        RestAssured.given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("data.size()", is(0));
    }

    @Test
    @DisplayName("전체 예약 목록을 조회할 수 있다.")
    void 예약_전체_목록_조회() {
        jdbcTemplate.update("INSERT INTO reservation (date, member_id, theme_id, time_id, store_id) VALUES ('2026-08-04', 1, 1, 1, 1)");
        jdbcTemplate.update("INSERT INTO reservation (date, member_id, theme_id, time_id, store_id) VALUES ('2026-08-05', 1, 1, 2, 1)");

        RestAssured.given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("data.size()", is(2));
    }

    @Test
    @DisplayName("예약을 수정할 수 있다.")
    void 예약_수정_성공() {
        jdbcTemplate.update("INSERT INTO reservation (date, member_id, theme_id, time_id, store_id) VALUES ('2026-08-04', 1, 1, 1, 1)");

        Map<String, Object> params = new HashMap<>();
        params.put("date", "2026-08-08");
        params.put("timeId", 1);
        params.put("themeId", 1);
        params.put("storeId", 1);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken)
                .body(params)
                .when().put("/reservations/1")
                .then().log().all()
                .statusCode(200)
                .body("data.date", is("2026-08-08"));
    }

    @Test
    @DisplayName("예약을 삭제할 수 있다.")
    void 예약_삭제_성공() {
        Map<String, Object> params = new HashMap<>();
        params.put("date", "2026-08-04");
        params.put("timeId", 1);
        params.put("themeId", 1);
        params.put("storeId", 1);

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
    }

    @Test
    @DisplayName("예약이 없는 이름으로 내 예약을 조회하면 빈 결과를 반환한다.")
    void 예약_없는_이름으로_내_예약_조회시_빈_결과() {
        RestAssured.given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .when().get("/reservations/mine?name=은오")
                .then().log().all()
                .statusCode(200)
                .body("data.size()", is(0));
    }

    @Test
    @DisplayName("예약이 없으면 가용 시간 조회 시 전체 시간을 반환한다.")
    void 예약_전에는_모든_시간_조회됨() {
        RestAssured.given().log().all()
                .when().get("/times?themeId=1&date=2026-06-04")
                .then().log().all()
                .statusCode(200)
                .body("data.size()", is(2));
    }

    @Test
    @DisplayName("중복된 날짜, 시간, 테마인 경우 예약할 수 없다.")
    void 예약_생성_실패_중복_예약() {
        Map<String, Object> params = new HashMap<>();
        params.put("date", "2026-08-04");
        params.put("timeId", 1);
        params.put("themeId", 1);
        params.put("storeId", 1);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken)
                .body(params)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(200);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken)
                .body(params)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("ok", is(false));
    }

    @Test
    @DisplayName("과거 날짜인 경우 예약할 수 없다.")
    void 예약_생성_실패_과거_날짜() {
        Map<String, Object> params = new HashMap<>();
        params.put("date", "2020-01-01");
        params.put("timeId", 1);
        params.put("themeId", 1);
        params.put("storeId", 1);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken)
                .body(params)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("ok", is(false));
    }

    @Test
    @DisplayName("존재하지 않는 예약 시간인 경우 예약할 수 없다.")
    void 예약_생성_실패_존재하지_않는_시간() {
        Map<String, Object> params = new HashMap<>();
        params.put("date", "2026-08-04");
        params.put("timeId", 999);
        params.put("themeId", 1);
        params.put("storeId", 1);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken)
                .body(params)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("ok", is(false));
    }

    @Test
    @DisplayName("존재하지 않는 테마인 경우 예약할 수 없다.")
    void 예약_생성_실패_존재하지_않는_테마() {
        Map<String, Object> params = new HashMap<>();
        params.put("date", "2026-08-04");
        params.put("timeId", 1);
        params.put("themeId", 999);
        params.put("storeId", 1);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken)
                .body(params)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("ok", is(false));
    }

    @Test
    @DisplayName("존재하지 않는 예약인 경우 삭제할 수 없다.")
    void 예약_삭제_실패_존재하지_않는_예약() {
        RestAssured.given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .when().delete("/reservations/999")
                .then().log().all()
                .statusCode(200)
                .body("ok", is(false));
    }
}
