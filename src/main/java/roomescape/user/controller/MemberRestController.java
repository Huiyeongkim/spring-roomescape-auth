package roomescape.user.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.common.dto.ApiResponse;
import roomescape.user.dto.MemberCreateRequest;
import roomescape.user.dto.MemberResponse;
import roomescape.user.service.MemberService;

@RestController
public class MemberRestController {

    private final MemberService memberService;

    public MemberRestController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/members")
    public ApiResponse<MemberResponse> createMember(@RequestBody MemberCreateRequest request) {
        MemberResponse memberResponse = memberService.createMember(request);
        return new ApiResponse<>(memberResponse);
    }
}
