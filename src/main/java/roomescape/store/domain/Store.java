package roomescape.store.domain;

import roomescape.member.domain.Member;

public class Store {

    private final Long id;
    private final String name;
    private final Member manager;

    public Store(Long id, String name, Member manager) {
        this.id = id;
        this.name = name;
        this.manager = manager;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Member getManager() {
        return manager;
    }
}
