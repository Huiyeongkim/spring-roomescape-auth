package roomescape.store.repository;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRole;
import roomescape.store.domain.Store;

import javax.sql.DataSource;
import java.util.Optional;

@Repository
public class StoreQueryingDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public StoreQueryingDao(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    private final RowMapper<Store> storeRowMapper = (resultSet, rowNum) -> {
        Member member = new Member(
                resultSet.getLong("member_id"),
                resultSet.getString("login_id"),
                resultSet.getString("password"),
                resultSet.getString("member_name"),
                MemberRole.valueOf(resultSet.getString("role"))
        );

        return new Store(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                member
        );
    };

    public boolean existsByName(String name) {
        String sql = """
                SELECT count(1) FROM store
                WHERE name = :name
                """;
        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("name", name);
        Integer count = jdbcTemplate.queryForObject(sql, param, Integer.class);
        return count != null && count > 0;
    }

    public Optional<Store> findById(Long id) {
        String sql = """
                SELECT s.id, s.name, 
                       m.id as member_id, m.login_id, m.password, m.name as member_name, m.role
                FROM store as s
                INNER JOIN member as m ON s.member_id = m.id
                WHERE s.id = :id
                """;
        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("id", id);

        try {
            Store store = jdbcTemplate.queryForObject(sql, param, storeRowMapper);
            return Optional.of(store);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
