package roomescape.theme.controller.api;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import roomescape.common.auth.LoginMember;
import roomescape.common.dto.ApiResponse;
import roomescape.member.domain.Member;
import roomescape.theme.dto.ThemeCreateRequest;
import roomescape.theme.dto.ThemeResponse;
import roomescape.theme.service.ThemeService;

import java.util.List;

@RequestMapping("/admin/themes")
@RestController
public class AdminThemeRestController {

    private final ThemeService themeService;

    public AdminThemeRestController(ThemeService themeService) {
        this.themeService = themeService;
    }

    @PostMapping
    public ApiResponse<ThemeResponse> create(@Valid @RequestBody ThemeCreateRequest themeRequest, @LoginMember Member member) {
        return new ApiResponse<>(themeService.create(themeRequest));
    }

    @GetMapping
    public ApiResponse<List<ThemeResponse>> readAll() {
        return new ApiResponse<>(themeService.findAll());
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id, @LoginMember Member member) {
        themeService.delete(id);
        return new ApiResponse<>(null);
    }
}

