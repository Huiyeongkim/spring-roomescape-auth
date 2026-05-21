package roomescape.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import roomescape.common.exception.AccessDeniedException;
import roomescape.common.exception.BusinessException;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRole;
import roomescape.store.domain.Store;
import roomescape.store.dto.StoreCreateRequest;
import roomescape.store.dto.StoreResponse;
import roomescape.store.repository.StoreQueryingDao;
import roomescape.store.repository.StoreUpdatingDao;
import roomescape.store.service.StoreService;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StoreServiceTest {

    @Mock
    StoreUpdatingDao storeUpdatingDao;

    @Mock
    StoreQueryingDao storeQueryingDao;

    @InjectMocks
    StoreService storeService;

    Member adminMember = new Member(1L, "admin", "pass", "관리자", MemberRole.ADMIN);
    Member userMember = new Member(2L, "user", "pass", "일반유저", MemberRole.USER);

    @Test
    @DisplayName("ADMIN 권한의 멤버는 매장을 생성할 수 있다.")
    void 매장_생성_성공() {
        // given
        StoreCreateRequest request = new StoreCreateRequest("브라운 매장");
        Store store = new Store(1L, "브라운 매장", adminMember);

        when(storeQueryingDao.existsByName(request.getName())).thenReturn(false);
        when(storeUpdatingDao.save(any(), anyLong())).thenReturn(1L);
        when(storeQueryingDao.findById(1L)).thenReturn(Optional.of(store));

        // when
        StoreResponse response = storeService.createStore(request, adminMember);

        // then
        Assertions.assertEquals("브라운 매장", response.getName());
    }

    @Test
    @DisplayName("매장 생성 시 이미 존재하는 이름이면 에러를 반환한다.")
    void 매장_생성_에러_이름_중복() {
        // given
        StoreCreateRequest request = new StoreCreateRequest("브라운 매장");

        when(storeQueryingDao.existsByName(request.getName())).thenReturn(true);

        // when && then
        Assertions.assertThrows(BusinessException.class, () -> storeService.createStore(request, adminMember));
    }

    @Test
    @DisplayName("ADMIN 권한이 없는 멤버는 매장을 생성할 수 없다.")
    void 매장_생성_에러_권한_없음() {
        // given
        StoreCreateRequest request = new StoreCreateRequest("브라운 매장");

        when(storeQueryingDao.existsByName(request.getName())).thenReturn(false);

        // when && then
        Assertions.assertThrows(AccessDeniedException.class, () -> storeService.createStore(request, userMember));
    }
}
