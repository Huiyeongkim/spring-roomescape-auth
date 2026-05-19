package roomescape.member.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.common.dto.ApiResponse;
import roomescape.member.domain.Member;
import roomescape.member.dto.LoginRequest;
import roomescape.member.dto.MemberCreateRequest;
import roomescape.member.dto.MemberResponse;
import roomescape.member.service.MemberService;

@RestController
public class MemberRestController {

    private static final String LOGIN_MEMBER_ID = "loginMemberId";
    private final MemberService memberService;

    public MemberRestController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/members")
    public ApiResponse<MemberResponse> createMember(@RequestBody MemberCreateRequest request) {
        MemberResponse memberResponse = memberService.createMember(request);
        return new ApiResponse<>(memberResponse);
    }

    @PostMapping("/login")
    public ApiResponse<Void> login(@RequestBody LoginRequest request, HttpSession session) {
        Member member = memberService.login(request);
        session.setAttribute(LOGIN_MEMBER_ID, member.getId());
        return new ApiResponse<>(null);
    }
}
