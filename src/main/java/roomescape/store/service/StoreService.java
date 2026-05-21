package roomescape.store.service;

import org.springframework.stereotype.Service;
import roomescape.common.exception.AccessDeniedException;
import roomescape.common.exception.BusinessException;
import roomescape.common.exception.ErrorCode;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRole;
import roomescape.store.domain.Store;
import roomescape.store.dto.StoreCreateRequest;
import roomescape.store.dto.StoreResponse;
import roomescape.store.repository.StoreQueryingDao;
import roomescape.store.repository.StoreUpdatingDao;

@Service
public class StoreService {

    private final StoreUpdatingDao storeUpdatingDao;
    private final StoreQueryingDao storeQueryingDao;

    public StoreService(StoreUpdatingDao storeUpdatingDao, StoreQueryingDao storeQueryingDao) {
        this.storeUpdatingDao = storeUpdatingDao;
        this.storeQueryingDao = storeQueryingDao;
    }

    public StoreResponse createStore(StoreCreateRequest request, Member member) {
        if (storeQueryingDao.existsByName(request.getName())) {
            throw new BusinessException(ErrorCode.STORE_ALREADY_EXISTS);
        }

        if (member.getRole() != MemberRole.ADMIN) {
            throw new AccessDeniedException();
        }

        Long savedId = storeUpdatingDao.save(request.getName(), member.getId());
        Store store = storeQueryingDao.findById(savedId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));
        return StoreResponse.from(store);
    }
}
