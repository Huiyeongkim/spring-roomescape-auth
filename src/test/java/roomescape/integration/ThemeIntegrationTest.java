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
class ThemeIntegrationTest {

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
    @DisplayName("테마를 생성할 수 있다.")
    void 테마_생성() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "무서운 이야기");
        params.put("description", "공포");
        params.put("url", "http://example.com");

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken)
                .body(params)
                .when().post("/admin/themes")
                .then().log().all()
                .statusCode(200)
                .body("data.name", is("무서운 이야기"))
                .body("data.description", is("공포"))
                .body("data.url", is("http://example.com"));
    }

    @Test
    @DisplayName("테마가 없으면 목록 조회 시 빈 결과를 반환한다.")
    void 테마_없을때_목록_조회() {
        RestAssured.given().log().all()
                .when().get("/themes")
                .then().log().all()
                .statusCode(200)
                .body("data.size()", is(0));
    }

    @Test
    @DisplayName("예약이 없으면 인기 테마 조회 시 빈 결과를 반환한다.")
    void 인기_테마_없을때_조회() {
        RestAssured.given().log().all()
                .when().get("/themes/popular")
                .then().log().all()
                .statusCode(200)
                .body("data.size()", is(0));
    }

    @Test
    @DisplayName("테마 목록을 조회할 수 있다.")
    void 테마_목록_조회() {
        jdbcTemplate.update("INSERT INTO theme (name, description, url) VALUES ('무서운 이야기', '공포', 'http://example.com')");
        jdbcTemplate.update("INSERT INTO theme (name, description, url) VALUES ('해적왕의 보물', '모험', 'http://example.com/2')");

        RestAssured.given().log().all()
                .when().get("/themes")
                .then().log().all()
                .statusCode(200)
                .body("data.size()", is(2));
    }

    @Test
    @DisplayName("테마를 삭제할 수 있다.")
    void 테마_삭제_성공() {
        jdbcTemplate.update("INSERT INTO theme (name, description, url) VALUES ('무서운 이야기', '공포', 'http://example.com')");

        RestAssured.given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .when().delete("/admin/themes/1")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    @DisplayName("이름이 없는 경우 테마를 생성할 수 없다.")
    void 테마_생성_실패_이름_없음() {
        Map<String, String> params = new HashMap<>();
        params.put("description", "공포");
        params.put("url", "http://example.com");

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken)
                .body(params)
                .when().post("/admin/themes")
                .then().log().all()
                .statusCode(400);
    }

    @Test
    @DisplayName("존재하지 않는 테마인 경우 삭제할 수 없다.")
    void 테마_삭제_실패_존재하지_않는_테마() {
        RestAssured.given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .when().delete("/admin/themes/999")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    @DisplayName("예약이 있는 테마인 경우 삭제할 수 없다.")
    void 테마_삭제_실패_예약이_있는_테마() {
        jdbcTemplate.update("INSERT INTO reservation_time (start_at) VALUES ('10:00')");
        jdbcTemplate.update("INSERT INTO theme (name, description, url) VALUES ('무서운 이야기', '공포', 'http://example.com')");
        jdbcTemplate.update("INSERT INTO store (name, member_id) VALUES ('브라운 매장', 1)");
        jdbcTemplate.update("INSERT INTO reservation (date, member_id, theme_id, time_id, store_id) VALUES ('2026-08-04', 1, 1, 1, 1)");

        RestAssured.given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .when().delete("/admin/themes/1")
                .then().log().all()
                .statusCode(200)
                .body("ok", is(false));
    }
}
