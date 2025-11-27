package ch.ffhs.authentification_service.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;

import static org.junit.jupiter.api.Assertions.*;

class JwtValidatorTest {

    @Test
    void constructor_throws_when_secret_too_short() {
        String shortSecret = "short";
        assertThrows(IllegalArgumentException.class, () -> new JwtValidator(shortSecret));
    }

    @Test
    void isValid_and_extracts_claims_for_valid_token() {
        String secret = "01234567890123456789012345678901"; // 32 bytes
        JwtValidator validator = new JwtValidator(secret);

        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        String token = Jwts.builder()
                .setSubject("alice")
                .claim("userId", "1111-2222")
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        assertTrue(validator.isValid(token));
        assertEquals("alice", validator.extractUsername(token));
        assertEquals("1111-2222", validator.extractUserId(token));
    }

    @Test
    void extractUsername_uses_username_claim_when_subject_missing() {
        String secret = "01234567890123456789012345678901";
        JwtValidator validator = new JwtValidator(secret);
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());

        String token = Jwts.builder()
                .claim("username", "bob")
                .claim("userId", "u-1")
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        assertTrue(validator.isValid(token));
        assertEquals("bob", validator.extractUsername(token));
    }

    @Test
    void isValid_returns_false_for_malformed_token() {
        String secret = "01234567890123456789012345678901";
        JwtValidator validator = new JwtValidator(secret);

        assertFalse(validator.isValid("not-a-token"));
    }
}

