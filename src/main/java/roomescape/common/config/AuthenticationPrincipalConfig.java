package roomescape.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import roomescape.common.auth.LoginCheckInterceptor;
import roomescape.common.auth.LoginMemberArgumentResolver;

import java.util.List;

@Configuration
public class AuthenticationPrincipalConfig implements WebMvcConfigurer {

    private final LoginCheckInterceptor loginCheckInterceptor;
    private final LoginMemberArgumentResolver loginMemberArgumentResolver;

    public AuthenticationPrincipalConfig(LoginCheckInterceptor loginCheckInterceptor, LoginMemberArgumentResolver loginMemberArgumentResolver) {
        this.loginCheckInterceptor = loginCheckInterceptor;
        this.loginMemberArgumentResolver = loginMemberArgumentResolver;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginCheckInterceptor)
                .addPathPatterns(
                        "/reservations/**",
                        "/admin/**"
                )
                .excludePathPatterns(
                        "/login",
                        "/logout",
                        "/token/refresh",
                        "/themes/popular"
                );
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginMemberArgumentResolver);
    }
}
