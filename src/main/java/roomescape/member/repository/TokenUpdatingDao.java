package roomescape.member.repository;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.LocalDateTime;

@Repository
public class TokenUpdatingDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public TokenUpdatingDao(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public void save(Long memberId, String refreshToken) {
        String sql = """
              INSERT INTO refresh_token(member_id, token, expired_at) 
              VALUES (:member_id, :token, :expired_at);
              """;
        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("member_id", memberId)
                .addValue("token", refreshToken)
                .addValue("expired_at", LocalDateTime.now().plusDays(14));

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, param, keyHolder, new String[]{"id"});
    }

    public void delete(String refreshToken) {
        String sql = "DELETE FROM refresh_token WHERE token = :token";
        jdbcTemplate.update(sql, new MapSqlParameterSource().addValue("token", refreshToken));
    }
}
