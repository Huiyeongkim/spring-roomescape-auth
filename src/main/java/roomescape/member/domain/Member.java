package roomescape.member.domain;

import roomescape.common.exception.BusinessException;
import roomescape.common.exception.ErrorCode;

public class Member {

    private final Long id;
    private final String loginId;
    private final String password;
    private final String name;
    private final MemberRole role;

    public Member(Long id, String loginId, String password, String name, MemberRole role) {
        validateName(name);
        this.id = id;
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.role = role;
    }

    private void validateName(String name) {
        if (!name.matches("^[a-zA-Z0-9가-힣]+$")) {
            throw new BusinessException(ErrorCode.RESERVATION_NAME_INVALID);
        }
    }

    public Long getId() {
        return id;
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

    public MemberRole getRole() {
        return role;
    }
}
