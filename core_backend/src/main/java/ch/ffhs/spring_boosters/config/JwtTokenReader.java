package ch.ffhs.spring_boosters.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class JwtTokenReader {

    @Value("${jwt.secret:}")
    private String secret;

    public Claims getClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getUserId(String token) {
        return getClaims(token).get("userid", String.class);
    }

    public String getUsername(String token) {
        return getClaims(token).get("username", String.class);
    }
}