package roomescape.store.dto;

import roomescape.store.domain.Store;

public class StoreResponse {
    
    private final String name;

    private StoreResponse(String name) {
        this.name = name;
    }

    public static StoreResponse from(Store store) {
        return new StoreResponse(store.getName());
    }
    
    public String getName() {
        return name;
    }
}
