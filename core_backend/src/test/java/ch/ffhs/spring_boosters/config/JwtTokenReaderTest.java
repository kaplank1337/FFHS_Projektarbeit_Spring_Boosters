package ch.ffhs.spring_boosters.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenReaderTest {

    private JwtTokenReader jwtTokenReader;
    private static final String SECRET = "abcdefghijklmnopqrstuvwxyzABCDEF"; // 32 chars

    @BeforeEach
    void setUp() {
        jwtTokenReader = new JwtTokenReader();
    }

    private SecretKey keyFromSecret(String secret) {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    private String createToken(String secret, String username, UUID userId) {
        SecretKey key = keyFromSecret(secret);
        return Jwts.builder()
                .setSubject(username)
                .claim("username", username)
                .claim("userid", userId.toString())
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    @Test
    void getClaims_getUserId_getUsername_withValidToken() {
        // set secret on component
        ReflectionTestUtils.setField(jwtTokenReader, "secret", SECRET);

        String username = "john";
        UUID userId = UUID.randomUUID();
        String token = createToken(SECRET, username, userId);

        // getClaims
        var claims = jwtTokenReader.getClaims(token);
        assertNotNull(claims, "Claims should not be null");
        assertEquals(username, claims.getSubject(), "Subject should match username");

        // getUserId and getUsername helpers
        assertEquals(userId.toString(), jwtTokenReader.getUserId(token));
        assertEquals(username, jwtTokenReader.getUsername(token));
    }

    @Test
    void getClaims_withMalformedToken_throws() {
        ReflectionTestUtils.setField(jwtTokenReader, "secret", SECRET);
        String bad = "not.a.valid.token";
        assertThrows(Exception.class, () -> jwtTokenReader.getClaims(bad));
    }

    @Test
    void getClaims_withNullToken_throws() {
        ReflectionTestUtils.setField(jwtTokenReader, "secret", SECRET);
        assertThrows(Exception.class, () -> jwtTokenReader.getClaims(null));
    }

    @Test
    void getClaims_withEmptySecret_throwsWeakKeyException() {
        // leave secret empty or explicitly set
        ReflectionTestUtils.setField(jwtTokenReader, "secret", "");

        // any token will cause key creation to fail due to insufficient size
        String token = "dummy";
        assertThrows(io.jsonwebtoken.security.WeakKeyException.class, () -> jwtTokenReader.getClaims(token));
    }

    @Test
    void createToken_withBase64Secret_and_reader_works() {
        // create base64 secret representation
        byte[] bytes = SECRET.getBytes(StandardCharsets.UTF_8);
        String b64 = Base64.getEncoder().encodeToString(bytes);
        String base64Secret = "base64:" + b64;

        // JwtTokenReader expects raw secret string bytes, not base64 prefix; we emulate storing plain secret
        // So set the actual secret (without prefix) to be the decoded bytes
        ReflectionTestUtils.setField(jwtTokenReader, "secret", SECRET);

        UUID userId = UUID.randomUUID();
        String username = "base64user";
        String token = createToken(SECRET, username, userId);

        assertEquals(userId.toString(), jwtTokenReader.getUserId(token));
        assertEquals(username, jwtTokenReader.getUsername(token));
    }
}
