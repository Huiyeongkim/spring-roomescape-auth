package roomescape.member.service;

import org.springframework.stereotype.Service;
import roomescape.common.exception.BusinessException;
import roomescape.common.exception.ErrorCode;
import roomescape.member.domain.Member;
import roomescape.member.dto.LoginRequest;
import roomescape.member.dto.MemberResponse;
import roomescape.member.dto.MemberCreateRequest;
import roomescape.member.repository.MemberQueryingDao;
import roomescape.member.repository.MemberUpdatingDao;

@Service
public class MemberService {

    private final MemberQueryingDao memberQueryingDao;
    private final MemberUpdatingDao memberUpdatingDao;

    public MemberService(MemberQueryingDao memberQueryingDao, MemberUpdatingDao memberUpdatingDao) {
        this.memberQueryingDao = memberQueryingDao;
        this.memberUpdatingDao = memberUpdatingDao;
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

    public Member login(LoginRequest request) {
        Member findMember = memberQueryingDao.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_ID_OR_PASSWORD));
        if (!findMember.getPassword().equals(request.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_ID_OR_PASSWORD);
        }
        return findMember;
    }
}
