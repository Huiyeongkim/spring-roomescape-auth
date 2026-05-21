package roomescape.reservation.domain;

import roomescape.member.domain.Member;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.store.domain.Store;
import roomescape.theme.domain.Theme;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Reservation {

    private final Long id;
    private final LocalDate date;

    private final Member member;
    private final ReservationTime time;
    private final Theme theme;
    private final Store store;

    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public Reservation(Long id, LocalDate date, Member member, ReservationTime time, Theme theme, Store store, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.member = member;
        this.date = date;
        this.time = time;
        this.theme = theme;
        this.store = store;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public LocalDate getDate() {
        return date;
    }

    public ReservationTime getTime() {
        return time;
    }

    public Theme getTheme() {
        return theme;
    }

    public Store getStore() {
        return store;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
