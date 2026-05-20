package roomescape.member.service;

import org.springframework.stereotype.Service;
import roomescape.common.auth.JwtProvider;
import roomescape.common.exception.BusinessException;
import roomescape.common.exception.ErrorCode;
import roomescape.common.exception.UnauthorizedException;
import roomescape.member.domain.Member;
import roomescape.member.dto.LoginRequest;
import roomescape.member.dto.MemberResponse;
import roomescape.member.dto.MemberCreateRequest;
import roomescape.member.dto.TokenResponse;
import roomescape.member.repository.MemberQueryingDao;
import roomescape.member.repository.MemberUpdatingDao;
import roomescape.member.repository.TokenQueryingDao;
import roomescape.member.repository.TokenUpdatingDao;

import java.util.UUID;

@Service
public class MemberService {

    private final JwtProvider jwtProvider;

    private final MemberQueryingDao memberQueryingDao;
    private final MemberUpdatingDao memberUpdatingDao;

    private final TokenUpdatingDao tokenUpdatingDao;
    private final TokenQueryingDao tokenQueryingDao;

    public MemberService(JwtProvider jwtProvider, MemberQueryingDao memberQueryingDao, MemberUpdatingDao memberUpdatingDao, TokenUpdatingDao tokenUpdatingDao, TokenQueryingDao tokenQueryingDao) {
        this.jwtProvider = jwtProvider;
        this.memberQueryingDao = memberQueryingDao;
        this.memberUpdatingDao = memberUpdatingDao;
        this.tokenUpdatingDao = tokenUpdatingDao;
        this.tokenQueryingDao = tokenQueryingDao;
    }

    public MemberResponse createMember(MemberCreateRequest request) {
        if (memberQueryingDao.existsByLoginId(request.getLoginId())) {
            throw new BusinessException(ErrorCode.MEMBER_ALREADY_EXISTS);
        }

        Long savedMemberId = memberUpdatingDao.save(request);
        Member findMember = memberQueryingDao.findById(savedMemberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        return MemberResponse.from(findMember);
    }

    public TokenResponse login(LoginRequest request) {
        Member findMember = memberQueryingDao.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_ID_OR_PASSWORD));
        if (!findMember.getPassword().equals(request.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_ID_OR_PASSWORD);
        }

        String accessToken = jwtProvider.createToken(findMember.getId());
        String refreshToken = UUID.randomUUID().toString();
        tokenUpdatingDao.save(findMember.getId(), refreshToken);
        return TokenResponse.from(accessToken, refreshToken);
    }

    public void logout(String refreshToken) {
        tokenUpdatingDao.delete(refreshToken);
    }

    public String refresh(String refreshToken) {
        Long memberId = tokenQueryingDao.findMemberIdByToken(refreshToken)
                .orElseThrow(UnauthorizedException::new);
        return jwtProvider.createToken(memberId);
    }
}
