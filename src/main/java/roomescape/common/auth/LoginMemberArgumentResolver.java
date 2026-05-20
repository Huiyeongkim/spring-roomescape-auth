package roomescape.common.auth;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import roomescape.common.exception.UnauthorizedException;
import roomescape.member.domain.Member;
import roomescape.member.repository.MemberQueryingDao;

@Component
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtProvider jwtProvider;
    private final MemberQueryingDao memberQueryingDao;

    public LoginMemberArgumentResolver(JwtProvider jwtProvider, MemberQueryingDao memberQueryingDao) {
        this.jwtProvider = jwtProvider;
        this.memberQueryingDao = memberQueryingDao;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasAnnotation = parameter.hasParameterAnnotation(LoginMember.class);
        boolean isMemberType = Member.class.isAssignableFrom(parameter.getParameterType());

        return hasAnnotation && isMemberType;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException();
        }

        String token = authHeader.substring(7);
        if (!jwtProvider.isValid(token)) {
            throw new UnauthorizedException();
        }
        Long memberId = jwtProvider.getMemberId(token);
        return memberQueryingDao.findById(memberId)
                .orElseThrow(UnauthorizedException::new);
    }
}
