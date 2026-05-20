package roomescape.reservation.controller.api;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import roomescape.common.auth.LoginMember;
import roomescape.common.dto.ApiResponse;
import roomescape.member.domain.Member;
import roomescape.reservation.dto.ReservationCreateRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.dto.ReservationUpdateRequest;
import roomescape.reservation.service.ReservationService;

import java.util.List;

@RequestMapping("/reservations")
@RestController
public class ReservationRestController {

    private final ReservationService reservationService;

    public ReservationRestController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ApiResponse<ReservationResponse> create(@Valid @RequestBody ReservationCreateRequest reservationReq, @LoginMember Member member) {
        return new ApiResponse<>(reservationService.create(reservationReq, member.getId()));
    }

    @GetMapping("/{id}")
    public ApiResponse<ReservationResponse> read(@PathVariable Long id, @LoginMember Member member) {
        return new ApiResponse<>(reservationService.read(id, member.getId()));
    }

    @GetMapping
    public ApiResponse<List<ReservationResponse>> readAll(@LoginMember Member member) {
        return new ApiResponse<>(reservationService.readAll());
    }

    @GetMapping("/mine")
    public ApiResponse<List<ReservationResponse>> readMyReservations(
            @LoginMember Member member
    ) {
        return new ApiResponse<>(reservationService.readMyReservations(member.getId()));
    }

    @PutMapping("/{id}")
    public ApiResponse<ReservationResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ReservationUpdateRequest newReservationReq,
            @LoginMember Member member
    ) {
        return new ApiResponse<>(reservationService.update(id, newReservationReq, member.getId()));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id, @LoginMember Member member) {
        reservationService.delete(id, member.getId());
        return new ApiResponse<>(null);
    }
}
