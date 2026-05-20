package roomescape.member.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.common.dto.ApiResponse;
import roomescape.member.dto.*;
import roomescape.member.service.MemberService;

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

    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(@RequestBody LoginRequest request) {
        TokenResponse token = memberService.login(request);
        return new ApiResponse<>(token);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestBody RefreshRequest request) {
        memberService.logout(request.getRefreshToken());
        return new ApiResponse<>(null);
    }

    @PostMapping("/token/refresh")
    public ApiResponse<String> refresh(@RequestBody RefreshRequest request, HttpServletResponse response) {
        String newAccessToken = memberService.refresh(request.getRefreshToken());
        response.setHeader("Authorization", "Bearer " + newAccessToken);
        return new ApiResponse<>(null);
    }
}
