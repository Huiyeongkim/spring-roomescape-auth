package roomescape.reservation.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class ReservationUpdateRequest {

    @NotNull(message = "예약 날짜는 필수입니다.")
    private final LocalDate date;

    @NotNull(message = "예약 시간은 필수입니다.")
    private final Long timeId;

    @NotNull(message = "테마는 필수입니다.")
    private final Long themeId;

    @NotNull(message = "매장은 필수입니다.")
    private final Long storeId;

    public ReservationUpdateRequest(LocalDate date, Long timeId, Long themeId, Long storeId) {
        this.date = date;
        this.timeId = timeId;
        this.themeId = themeId;
        this.storeId = storeId;
    }

    public LocalDate getDate() {
        return date;
    }

    public Long getTimeId() {
        return timeId;
    }

    public Long getThemeId() {
        return themeId;
    }

    public Long getStoreId() {
        return storeId;
    }
}
