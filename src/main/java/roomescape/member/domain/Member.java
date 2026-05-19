package roomescape.member.domain;

public class Member {

    private final Long id;
    private final String loginId;
    private final String password;
    private final String name;
    private final MemberRole role;

    public Member(Long id, String loginId, String password, String name, MemberRole role) {
        this.id = id;
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.role = role;
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
