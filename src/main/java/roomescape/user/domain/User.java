package roomescape.user.domain;

public class User {

    private final Long id;
    private final String name;
    private final UserRole role;

    public User(Long id, String name, UserRole role) {
        this.id = id;
        this.name = name;
        this.role = role;
    }
}
