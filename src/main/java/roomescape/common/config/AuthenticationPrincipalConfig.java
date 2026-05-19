package roomescape.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import roomescape.common.auth.LoginCheckInterceptor;

@Configuration
public class AuthenticationPrincipalConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(new LoginCheckInterceptor())
                .addPathPatterns(
                        "/reservations/**",
                        "/admin/**"
                )
                .excludePathPatterns(
                        "/login",
                        "/logout",
                        "/themes/popular"
                );
    }
}
