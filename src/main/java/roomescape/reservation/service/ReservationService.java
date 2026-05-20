package roomescape.reservation.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.dto.ReservationCreateRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.dto.ReservationUpdateRequest;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.theme.domain.Theme;
import roomescape.common.exception.BusinessException;
import roomescape.common.exception.ErrorCode;
import roomescape.reservation.repository.ReservationQueryingDao;
import roomescape.reservationtime.repository.ReservationTimeQueryingDao;
import roomescape.reservation.repository.ReservationUpdatingDao;
import roomescape.theme.repository.ThemeQueryingDao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Transactional(readOnly = true)
@Service
public class ReservationService {

    private final ReservationQueryingDao reservationQueryingDao;
    private final ReservationUpdatingDao reservationUpdatingDao;
    private final ReservationTimeQueryingDao reservationTimeQueryingDao;
    private final ThemeQueryingDao themeQueryingDao;

    public ReservationService(ReservationQueryingDao reservationQueryingDao, ReservationUpdatingDao reservationUpdatingDao, ReservationTimeQueryingDao reservationTimeQueryingDao, ThemeQueryingDao themeQueryingDao) {
        this.reservationQueryingDao = reservationQueryingDao;
        this.reservationUpdatingDao = reservationUpdatingDao;
        this.reservationTimeQueryingDao = reservationTimeQueryingDao;
        this.themeQueryingDao = themeQueryingDao;
    }

    @Transactional
    public ReservationResponse create(ReservationCreateRequest reservationReq, Long memberId) {
        ReservationTime findReservationTime = findReservationTimeOrThrow(reservationReq.getTimeId());
        Theme findTheme = findThemeOrThrow(reservationReq.getThemeId());

        validateNotPast(reservationReq.getDate(), findReservationTime.getStartAt());

        validateNoDuplicate(findTheme, reservationReq.getDate(), findReservationTime);

        Long generatedId;
        try {
            generatedId = reservationUpdatingDao.save(reservationReq, memberId);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException(ErrorCode.RESERVATION_ALREADY_EXISTS);
        }

        return ReservationResponse.from(reservationQueryingDao.findReservationById(generatedId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND)));
    }

    public ReservationResponse read(Long id, Long memberId) {
        Reservation reservationById = reservationQueryingDao.findReservationById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));
        return ReservationResponse.from(reservationById);
    }

    public List<ReservationResponse> readAll() {
        List<Reservation> reservations = reservationQueryingDao.findAllReservations();
        return reservations.stream()
                .map(ReservationResponse::from)
                .toList();
    }

    public List<ReservationResponse> readMyReservations(Long memberId) {
        List<Reservation> reservations = reservationQueryingDao.findMyReservations(memberId);
        return reservations.stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @Transactional
    public ReservationResponse update(Long id, ReservationUpdateRequest newReservationReq, Long memberId) {
        if (!reservationQueryingDao.existsById(id)) {
            throw new BusinessException(ErrorCode.RESERVATION_NOT_FOUND);
        }

        ReservationTime findReservationTime = findReservationTimeOrThrow(newReservationReq.getTimeId());
        Theme findTheme = findThemeOrThrow(newReservationReq.getThemeId());

        validateNotPast(newReservationReq.getDate(), findReservationTime.getStartAt());

        validateNoDuplicate(findTheme, newReservationReq.getDate(), findReservationTime);

        try {
            reservationUpdatingDao.update(id, newReservationReq);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException(ErrorCode.RESERVATION_ALREADY_EXISTS);
        }

        return ReservationResponse.from(reservationQueryingDao.findReservationById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND)));
    }

    @Transactional
    public void delete(Long id, Long memberId) {
        Reservation findReservation = reservationQueryingDao.findReservationById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));
        ReservationTime reservationTime = findReservation.getTime();

        validateNotPast(findReservation.getDate(), reservationTime.getStartAt());

        reservationUpdatingDao.delete(id);
    }

    private void validateNotPast(LocalDate date, LocalTime time) {
        LocalDateTime criteria = LocalDateTime.of(date, time);
        if (!criteria.isAfter(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.RESERVATION_DATE_PAST);
        }
    }

    private void validateNoDuplicate(Theme theme, LocalDate date, ReservationTime reservationTime) {
        reservationQueryingDao.findReservationByThemeAndDateAndTime(theme.getId(), date, reservationTime.getId())
                .ifPresent(reservation -> {
                    throw new BusinessException(ErrorCode.RESERVATION_ALREADY_EXISTS);
                });
    }

    private ReservationTime findReservationTimeOrThrow(Long reservationTimeId) {
        return reservationTimeQueryingDao.findReservationTimeById(reservationTimeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_TIME_NOT_FOUND));
    }

    private Theme findThemeOrThrow(Long themeId) {
        return themeQueryingDao.findThemeById(themeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.THEME_NOT_FOUND));
    }
}
