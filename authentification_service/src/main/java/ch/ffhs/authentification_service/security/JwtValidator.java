package ch.ffhs.authentification_service.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;

/**
 * JWT-Validator für Gateway - NUR VALIDIERUNG, keine Token-Generierung!
 * Token-Generierung erfolgt im Core Backend.
 */
@Component
public class JwtValidator {

    private final Key key;

    public JwtValidator(@Value("${jwt.secret}") String secret) {
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
            throw new IllegalArgumentException("JWT secret ist zu kurz (<256 Bit). Verwende mindestens 32 Byte.");
        }
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Validiert JWT-Token (Signatur + Ablaufzeit)
     */
    public boolean isValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Extrahiert Username aus Token
     */
    public String extractUsername(String token) {
        Claims claims = parseClaims(token);
        // Versuche zuerst "sub", dann "username"
        String username = claims.getSubject();
        if (username == null || username.isEmpty()) {
            username = claims.get("username", String.class);
        }
        return username;
    }

    /**
     * Extrahiert User-ID aus Token
     */
    public String extractUserId(String token) {
        Claims claims = parseClaims(token);
        return claims.get("userId", String.class);
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

