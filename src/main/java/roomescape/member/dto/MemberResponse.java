package roomescape.member.dto;

import roomescape.member.domain.Member;

public class MemberResponse {

    private final Long id;
    private final String loginId;
    private final String name;

    private MemberResponse(Long id, String loginId, String name) {
        this.id = id;
        this.loginId = loginId;
        this.name = name;
    }

    public static MemberResponse from(Member member) {
        return new MemberResponse(member.getId(), member.getLoginId(), member.getName());
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
}
