package roomescape.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class MemberCreateRequest {

    private final String loginId;
    private final String password;

    @Size(max = 255, message = "이름은 255자 이하여야 합니다.")
    @NotBlank(message = "이름은 필수입니다.")
    private final String name;

    public MemberCreateRequest(String loginId, String password, String name) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getLoginId() {
        return loginId;
    }

    public String getPassword() {
        return password;
    }
}
