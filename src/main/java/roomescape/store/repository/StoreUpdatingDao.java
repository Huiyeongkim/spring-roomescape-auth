package roomescape.store.repository;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class StoreUpdatingDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public StoreUpdatingDao(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public Long save(String name, Long memberId) {
        String sql = """
                INSERT INTO store(name, member_id) VALUES (:name, :member_id);
                """;
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("name", name)
                .addValue("member_id", memberId);
        jdbcTemplate.update(sql, param, keyHolder, new String[]{"id"});
        return keyHolder.getKey().longValue();
    }
}
