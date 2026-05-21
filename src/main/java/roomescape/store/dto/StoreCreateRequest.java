package roomescape.store.dto;

public class StoreCreateRequest {

    private final String name;

    public StoreCreateRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
