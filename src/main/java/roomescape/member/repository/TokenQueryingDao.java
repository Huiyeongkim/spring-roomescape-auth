package roomescape.member.repository;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Optional;

@Repository
public class TokenQueryingDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public TokenQueryingDao(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public Optional<Long> findMemberIdByToken(String token) {
        String sql = """
                SELECT member_id FROM refresh_token
                WHERE token = :token AND expired_at > NOW();
                """;
        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("token", token);

        try {
            Long memberId = jdbcTemplate.queryForObject(sql, param, Long.class);
            return Optional.of(memberId);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
