package roomescape.member.dto;

import roomescape.member.domain.Member;

public class MemberResponse {

    private final Long id;
    private final String name;

    private MemberResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static MemberResponse from(Member member) {
        return new MemberResponse(member.getId(), member.getName());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
