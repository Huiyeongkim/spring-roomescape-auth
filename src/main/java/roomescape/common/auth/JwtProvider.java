package roomescape.common.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtProvider {

    private final String secretKey = "roomescape-secret-key-for-jwt-auth";

    public String createToken(Long memberId) {
        return Jwts.builder()
                .claim("memberId", memberId)
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30)) // 30분
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .compact();
    }

    public boolean isValid(String token) {
        try {
            parse(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Long getMemberId(String token) {
        Claims claims = parse(token);
        return ((Number) claims.get("memberId")).longValue();
    }

    private Claims parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
