package roomescape.member.repository;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRole;

import javax.sql.DataSource;
import java.util.Optional;

@Repository
public class MemberQueryingDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public MemberQueryingDao(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    private final RowMapper<Member> memberRowMapper = (resultSet, rowNum) -> {
        Member member = new Member(
                resultSet.getLong("id"),
                resultSet.getString("loginId"),
                resultSet.getString("password"),
                resultSet.getString("name"),
                MemberRole.valueOf(resultSet.getString("role"))
        );
        return member;
    };

    public Optional<Member> findById(Long savedMemberId) {
        String sql = """
                SELECT id, name, role FROM member
                WHERE id = :id;
                """;

        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("id", savedMemberId);

        try {
            Member member = jdbcTemplate.queryForObject(sql, param, memberRowMapper);
            return Optional.of(member);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Member> findByLoginId(String loginId) {
        String sql = """
                SELECT id, login_id, password name, role FROM member
                WHERE login_id = :login_id;
                """;

        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("login_id", loginId);

        try {
            Member member = jdbcTemplate.queryForObject(sql, param, memberRowMapper);
            return Optional.of(member);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public boolean existsByName(String name) {
        String sql = """
                SELECT count(1) FROM member
                WHERE name = :name;
                """;
        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("name", name);

        Integer count = jdbcTemplate.queryForObject(sql, param, Integer.class);
        return count != null && count > 0;
    }

    public boolean existsByLoginId(String loginId) {
        String sql = """
                SELECT count(1) FROM member
                WHERE login_id = :login_id;
                """;
        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("login_id", loginId);

        Integer count = jdbcTemplate.queryForObject(sql, param, Integer.class);
        return count != null && count > 0;
    }
}
