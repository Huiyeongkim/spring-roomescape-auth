package roomescape.member.repository;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import roomescape.member.domain.MemberRole;

import javax.sql.DataSource;

@Repository
public class MemberUpdatingDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public MemberUpdatingDao(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public Long save(String name) {
        String sql = """
                INSERT INTO member(name, role) VALUES (:name, :role);
                """;

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("name", name)
                .addValue("role", MemberRole.USER.name());

        jdbcTemplate.update(sql, param, keyHolder, new String[]{"id"});
        return keyHolder.getKey().longValue();
    }
}
