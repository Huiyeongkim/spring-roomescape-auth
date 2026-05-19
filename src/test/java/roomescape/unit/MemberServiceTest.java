package roomescape.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import roomescape.common.exception.BusinessException;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRole;
import roomescape.member.dto.LoginRequest;
import roomescape.member.dto.MemberCreateRequest;
import roomescape.member.dto.MemberResponse;
import roomescape.member.repository.MemberQueryingDao;
import roomescape.member.repository.MemberUpdatingDao;
import roomescape.member.service.MemberService;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    MemberUpdatingDao memberUpdatingDao;

    @Mock
    MemberQueryingDao memberQueryingDao;

    @InjectMocks
    MemberService memberService;

    @Test
    @DisplayName("멤버를 생성할 수 있다.")
    void 멤버_생성_성공() {
        // given
        String loginId = "1234ll";
        String password = "12sdf";
        String name = "브라운";

        MemberCreateRequest request = new MemberCreateRequest(loginId, password, name);
        Member member = new Member(1L, loginId, password, name, MemberRole.USER);

        when(memberQueryingDao.existsByLoginId(request.getLoginId()))
                .thenReturn(false);
        when(memberQueryingDao.existsByName(request.getName()))
                .thenReturn(false);

        when(memberQueryingDao.findById(anyLong()))
                .thenReturn(Optional.of(member));

        // when
        MemberResponse response = memberService.createMember(request);

        // then
        Assertions.assertEquals(name, response.getName());
        Assertions.assertEquals(loginId, response.getLoginId());
    }

    @Test
    @DisplayName("멤버를 생성할 때 이미 아이디가 있으면 에러를 반환한다.")
    void 멤버_생성_에러_아이디_있음() {
        // given
        String loginId = "1234ll";
        String password = "12sdf";
        String name = "브라운";

        MemberCreateRequest request = new MemberCreateRequest(loginId, password, name);

        when(memberQueryingDao.existsByLoginId(request.getLoginId()))
                .thenReturn(true);

        // when && then
        Assertions.assertThrows(BusinessException.class, () -> memberService.createMember(request));
    }

    @Test
    @DisplayName("멤버를 생성할 때 이미 이름이 있으면 에러를 반환한다.")
    void 멤버_생성_에러_이름_있음() {
        // given
        String loginId = "1234ll";
        String password = "12sdf";
        String name = "브라운";

        MemberCreateRequest request = new MemberCreateRequest(loginId, password, name);

        when(memberQueryingDao.existsByLoginId(request.getLoginId()))
                .thenReturn(false);
        when(memberQueryingDao.existsByName(request.getName()))
                .thenReturn(true);

        // when && then
        Assertions.assertThrows(BusinessException.class, () -> memberService.createMember(request));
    }

    @Test
    @DisplayName("로그인할 수 있다.")
    void 로그인_성공() {
        // given
        String loginId = "1234ll";
        String password = "12sdf";
        String name = "브라운";

        LoginRequest loginRequest = new LoginRequest(loginId, password);
        Member member = new Member(1L, loginId, password, name, MemberRole.USER);

        when(memberQueryingDao.findByLoginId(loginRequest.getLoginId()))
                .thenReturn(Optional.of(member));

        // when
        Member response = memberService.login(loginRequest);

        // then
        Assertions.assertEquals(name, response.getName());
        Assertions.assertEquals(loginId, response.getLoginId());
    }

    @Test
    @DisplayName("로그인 시 아이디가 없으면 에러를 반환한다.")
    void 로그인_에러_아이디_없음() {
        // given
        String loginId = "1234ll";
        String password = "12sdf";

        LoginRequest loginRequest = new LoginRequest(loginId, password);

        when(memberQueryingDao.findByLoginId(loginRequest.getLoginId()))
                .thenReturn(Optional.empty());

        // when && then
        Assertions.assertThrows(BusinessException.class, () -> memberService.login(loginRequest));
    }

    @Test
    @DisplayName("로그인 시 비밀번호가 틀리면 에러를 반환한다.")
    void 로그인_에러_비밀번호_미일치() {
        // given
        String loginId = "1234ll";
        String password = "12sdf";
        String anotherPassword = "1dfs";
        String name = "브라운";

        LoginRequest loginRequest = new LoginRequest(loginId, anotherPassword);
        Member member = new Member(1L, loginId, password, name, MemberRole.USER);

        when(memberQueryingDao.findByLoginId(loginRequest.getLoginId()))
                .thenReturn(Optional.of(member));

        // when && then
        Assertions.assertThrows(BusinessException.class, () -> memberService.login(loginRequest));
    }
}
