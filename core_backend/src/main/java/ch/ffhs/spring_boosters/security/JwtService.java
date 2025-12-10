package ch.ffhs.spring_boosters.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    private final Key key;
    private final long ttlSeconds;

    public JwtService(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.expiration-seconds}") long ttlSeconds) {
        byte[] keyBytes;
        // Unterstützt Format: base64:<ENCODED>
        if (secret.startsWith("base64:")) {
            String b64 = secret.substring(7);
            keyBytes = Decoders.BASE64.decode(b64);
        } else {
            keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        }
        // Mindestlänge für HS256: 256 Bit = 32 Bytes
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("JWT secret ist zu kurz (<256 Bit). Verwende mindestens 32 Byte oder ein Base64-Secret mit Prefix 'base64:'.");
        }
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.ttlSeconds = ttlSeconds;
    }

    public String generateToken(String username, UUID userId) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(username)
                .claim("username", username)
                .claim("userid", userId.toString())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(ttlSeconds)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public String extractUserId(String token) {
        return parseClaims(token).get("userId", String.class);
    }

    public boolean isValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

