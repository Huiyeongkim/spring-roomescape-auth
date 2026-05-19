package roomescape.user.domain;

public class Member {

    private final Long id;
    private final String name;
    private final MemberRole role;

    public Member(Long id, String name, MemberRole role) {
        this.id = id;
        this.name = name;
        this.role = role;
    }
}
