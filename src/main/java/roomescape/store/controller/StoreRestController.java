package roomescape.store.controller;

import org.springframework.web.bind.annotation.*;
import roomescape.common.auth.LoginMember;
import roomescape.common.dto.ApiResponse;
import roomescape.member.domain.Member;
import roomescape.store.dto.StoreCreateRequest;
import roomescape.store.dto.StoreResponse;
import roomescape.store.service.StoreService;

@RequestMapping("/admin/stores")
@RestController
public class StoreRestController {

    private final StoreService storeService;

    public StoreRestController(StoreService storeService) {
        this.storeService = storeService;
    }

    @PostMapping
    public ApiResponse<StoreResponse> createStore(
            @RequestBody StoreCreateRequest request,
            @LoginMember Member member
    ) {
        StoreResponse response = storeService.createStore(request, member);
        return new ApiResponse<>(response);
    }

}
