package roomescape.reservation.repository;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import roomescape.reservation.dto.ReservationCreateRequest;
import roomescape.reservation.dto.ReservationUpdateRequest;

@Repository
public class ReservationUpdatingDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public ReservationUpdatingDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long save(ReservationCreateRequest reservationReq, Long memberId) {
        String sql = "insert into reservation(date, member_id, time_id, theme_id, store_id) values(:date, :member_id, :time_id, :theme_id, :store_id)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("date", reservationReq.getDate())
                .addValue("member_id", memberId)
                .addValue("time_id", reservationReq.getTimeId())
                .addValue("theme_id", reservationReq.getThemeId())
                .addValue("store_id", reservationReq.getStoreId());

        jdbcTemplate.update(sql, param, keyHolder, new String[]{"id"});
        return keyHolder.getKey().longValue();
    }

    public void update(Long id, ReservationUpdateRequest reservationReq) {
        String sql = "update reservation SET date = :date, time_id = :time_id, theme_id = :theme_id, store_id = :store_id where id = :id";
        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("date", reservationReq.getDate())
                .addValue("time_id", reservationReq.getTimeId())
                .addValue("theme_id", reservationReq.getThemeId())
                .addValue("store_id", reservationReq.getStoreId())
                .addValue("id", id);

        jdbcTemplate.update(sql, param);
    }

    public void delete(Long id) {
        String sql = "delete from reservation where id = :id";
        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("id", id);
        jdbcTemplate.update(sql, param);
    }
}
