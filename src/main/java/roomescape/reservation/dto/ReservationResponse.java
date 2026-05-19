package roomescape.reservation.dto;

import roomescape.member.dto.MemberResponse;
import roomescape.reservation.domain.Reservation;
import roomescape.reservationtime.dto.ReservationTimeResponse;
import roomescape.theme.dto.ThemeResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReservationResponse {

    private final Long id;
    private final LocalDate date;

    private final MemberResponse member;
    private final ReservationTimeResponse time;
    private final ThemeResponse theme;

    private final LocalDateTime createdAt;

    private ReservationResponse(Long id, LocalDate date, MemberResponse member, ReservationTimeResponse time, ThemeResponse theme, LocalDateTime createdAt) {
        this.id = id;
        this.date = date;

        this.member = member;
        this.time = time;
        this.theme = theme;
        this.createdAt = createdAt;
    }

    public static ReservationResponse from(Reservation reservation) {
        MemberResponse memberResponse = MemberResponse.from(reservation.getMember());
        ReservationTimeResponse reservationTimeResponse = ReservationTimeResponse.from(reservation.getTime());
        ThemeResponse themeResponse = ThemeResponse.from(reservation.getTheme());
        return new ReservationResponse(reservation.getId(), reservation.getDate(), memberResponse, reservationTimeResponse, themeResponse, reservation.getCreatedAt());
    }

    public Long getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public MemberResponse getMember() {
        return member;
    }

    public ReservationTimeResponse getTime() {
        return time;
    }

    public ThemeResponse getTheme() {
        return theme;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
