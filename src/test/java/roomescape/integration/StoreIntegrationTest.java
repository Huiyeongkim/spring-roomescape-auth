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
class StoreIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String adminToken;
    private String userToken;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("INSERT INTO member (login_id, password, name, role) VALUES ('admin1', 'pass1', '관리자', 'ADMIN')");
        jdbcTemplate.update("INSERT INTO member (login_id, password, name, role) VALUES ('user1', 'pass1', '일반유저', 'USER')");

        Map<String, String> adminLogin = new HashMap<>();
        adminLogin.put("loginId", "admin1");
        adminLogin.put("password", "pass1");

        adminToken = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(adminLogin)
                .when().post("/login")
                .then().statusCode(200)
                .extract().jsonPath().getString("data.accessToken");

        Map<String, String> userLogin = new HashMap<>();
        userLogin.put("loginId", "user1");
        userLogin.put("password", "pass1");

        userToken = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(userLogin)
                .when().post("/login")
                .then().statusCode(200)
                .extract().jsonPath().getString("data.accessToken");
    }

    @Test
    @DisplayName("ADMIN 권한으로 매장을 생성할 수 있다.")
    void 매장_생성_성공() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "브라운 매장");

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + adminToken)
                .body(params)
                .when().post("/admin/stores")
                .then().log().all()
                .statusCode(200)
                .body("data.name", is("브라운 매장"));
    }

    @Test
    @DisplayName("USER 권한으로는 매장을 생성할 수 없다.")
    void 매장_생성_실패_권한_없음() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "브라운 매장");

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + userToken)
                .body(params)
                .when().post("/admin/stores")
                .then().log().all()
                .statusCode(403);
    }

    @Test
    @DisplayName("이미 존재하는 이름으로 매장을 생성할 수 없다.")
    void 매장_생성_실패_이름_중복() {
        jdbcTemplate.update("INSERT INTO store (name, member_id) VALUES ('브라운 매장', 1)");

        Map<String, String> params = new HashMap<>();
        params.put("name", "브라운 매장");

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + adminToken)
                .body(params)
                .when().post("/admin/stores")
                .then().log().all()
                .statusCode(200)
                .body("ok", is(false));
    }

    @Test
    @DisplayName("로그인하지 않으면 매장을 생성할 수 없다.")
    void 매장_생성_실패_미인증() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "브라운 매장");

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/admin/stores")
                .then().log().all()
                .statusCode(401);
    }
}
