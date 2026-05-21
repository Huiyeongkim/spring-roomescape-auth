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

import java.time.LocalDate;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class PopularThemeIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("INSERT INTO member (login_id, password, name, role) VALUES ('id1', 'pass1', '브라운', 'USER')");
        jdbcTemplate.update("INSERT INTO reservation_time (start_at) VALUES ('10:00')");
        jdbcTemplate.update("INSERT INTO reservation_time (start_at) VALUES ('11:00')");
        jdbcTemplate.update("INSERT INTO reservation_time (start_at) VALUES ('12:00')");
        jdbcTemplate.update("INSERT INTO reservation_time (start_at) VALUES ('13:00')");
        jdbcTemplate.update("INSERT INTO theme (name, description, url) VALUES ('인형의 집', '공포 테마의 클래식', 'https://example.com/1')");
        jdbcTemplate.update("INSERT INTO theme (name, description, url) VALUES ('해적왕의 보물', '침몰한 해적선', 'https://example.com/2')");
        jdbcTemplate.update("INSERT INTO theme (name, description, url) VALUES ('명탐정의 부재', '사라진 명탐정', 'https://example.com/3')");
        jdbcTemplate.update("INSERT INTO theme (name, description, url) VALUES ('우주정거장', '산소가 고갈', 'https://example.com/4')");
        jdbcTemplate.update("INSERT INTO theme (name, description, url) VALUES ('꿈속의 과자집', '꿈속에서 길을 잃은', 'https://example.com/5')");
        jdbcTemplate.update("INSERT INTO store (name, member_id) VALUES ('브라운 매장', 1)");
    }

    @Test
    @DisplayName("7일 범위 내 예약 수 기준으로 내림차순 정렬하여 인기 테마를 조회할 수 있다.")
    void 인기_테마_조회_예약_수_내림차순() {
        LocalDate now = LocalDate.now();

        jdbcTemplate.update("INSERT INTO reservation (date, member_id, theme_id, time_id, store_id) VALUES (?, 1, 1, 1, 1)", now.minusDays(9));
        jdbcTemplate.update("INSERT INTO reservation (date, member_id, theme_id, time_id, store_id) VALUES (?, 1, 2, 3, 1)", now.minusDays(10));
        jdbcTemplate.update("INSERT INTO reservation (date, member_id, theme_id, time_id, store_id) VALUES (?, 1, 2, 2, 1)", now.minusDays(11));
        jdbcTemplate.update("INSERT INTO reservation (date, member_id, theme_id, time_id, store_id) VALUES (?, 1, 3, 2, 1)", now.minusDays(6));
        jdbcTemplate.update("INSERT INTO reservation (date, member_id, theme_id, time_id, store_id) VALUES (?, 1, 5, 4, 1)", now.minusDays(5));
        jdbcTemplate.update("INSERT INTO reservation (date, member_id, theme_id, time_id, store_id) VALUES (?, 1, 5, 1, 1)", now.minusDays(4));

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .when().get("/themes/popular")
                .then().log().all()
                .statusCode(200)
                .body("data.id", contains(5, 3));
    }

    @Test
    @DisplayName("7일 범위 내 예약 수가 동일한 경우 테마 id 오름차순으로 조회할 수 있다.")
    void 인기_테마_조회_예약_수_동일시_id_오름차순() {
        LocalDate now = LocalDate.now();

        jdbcTemplate.update("INSERT INTO reservation (date, member_id, theme_id, time_id, store_id) VALUES (?, 1, 1, 1, 1)", now.minusDays(9));
        jdbcTemplate.update("INSERT INTO reservation (date, member_id, theme_id, time_id, store_id) VALUES (?, 1, 2, 3, 1)", now.minusDays(10));
        jdbcTemplate.update("INSERT INTO reservation (date, member_id, theme_id, time_id, store_id) VALUES (?, 1, 3, 2, 1)", now.minusDays(3));
        jdbcTemplate.update("INSERT INTO reservation (date, member_id, theme_id, time_id, store_id) VALUES (?, 1, 3, 2, 1)", now.minusDays(6));
        jdbcTemplate.update("INSERT INTO reservation (date, member_id, theme_id, time_id, store_id) VALUES (?, 1, 5, 4, 1)", now.minusDays(5));
        jdbcTemplate.update("INSERT INTO reservation (date, member_id, theme_id, time_id, store_id) VALUES (?, 1, 5, 1, 1)", now.minusDays(4));

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .when().get("/themes/popular")
                .then().log().all()
                .statusCode(200)
                .body("data.id", contains(3, 5));
    }

    @Test
    @DisplayName("7일 범위 밖 예약인 경우 인기 테마 조회에 포함되지 않는다.")
    void 인기_테마_조회_범위_밖_예약_제외() {
        LocalDate now = LocalDate.now();

        jdbcTemplate.update("INSERT INTO reservation (date, member_id, theme_id, time_id, store_id) VALUES (?, 1, 1, 1, 1)", now.minusDays(9));
        jdbcTemplate.update("INSERT INTO reservation (date, member_id, theme_id, time_id, store_id) VALUES (?, 1, 2, 3, 1)", now.minusDays(10));
        jdbcTemplate.update("INSERT INTO reservation (date, member_id, theme_id, time_id, store_id) VALUES (?, 1, 2, 2, 1)", now.minusDays(11));
        jdbcTemplate.update("INSERT INTO reservation (date, member_id, theme_id, time_id, store_id) VALUES (?, 1, 3, 2, 1)", now.minusDays(6));
        jdbcTemplate.update("INSERT INTO reservation (date, member_id, theme_id, time_id, store_id) VALUES (?, 1, 5, 4, 1)", now.minusDays(5));
        jdbcTemplate.update("INSERT INTO reservation (date, member_id, theme_id, time_id, store_id) VALUES (?, 1, 5, 1, 1)", now.minusDays(4));

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .when().get("/themes/popular")
                .then().log().all()
                .statusCode(200)
                .body("data.size()", is(2));
    }
}
