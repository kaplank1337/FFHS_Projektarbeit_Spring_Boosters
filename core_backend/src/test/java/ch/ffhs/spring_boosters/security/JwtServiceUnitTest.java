package ch.ffhs.spring_boosters.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceUnitTest {

    private static final String SECRET = "abcdefghijklmnopqrstuvwxyzABCDEF"; // 32 chars
    private static final long TTL_SECONDS = 60L;
    private final ObjectMapper mapper = new ObjectMapper();

    private JsonNode decodePayload(String token) throws Exception {
        String[] parts = token.split("\\.");
        assertTrue(parts.length >= 2, "Token should have at least 2 parts");
        String payload = parts[1];
        byte[] decoded = Base64.getUrlDecoder().decode(payload);
        String json = new String(decoded, StandardCharsets.UTF_8);
        return mapper.readTree(json);
    }

    @Test
    void constructor_withShortSecret_throws() {
        String shortSecret = "short-secret";
        assertThrows(IllegalArgumentException.class, () -> new JwtService(shortSecret, TTL_SECONDS));
    }

    @Test
    void constructor_withBase64Secret_works() {
        byte[] bytes = SECRET.getBytes(StandardCharsets.UTF_8);
        String b64 = Base64.getEncoder().encodeToString(bytes);
        String secret = "base64:" + b64;

        JwtService svc = new JwtService(secret, TTL_SECONDS);
        assertNotNull(svc);

        // token can be generated
        String token = svc.generateToken("u", UUID.randomUUID());
        assertNotNull(token);
    }

    @Test
    void generateToken_and_payload_contains_expected_claims() throws Exception {
        JwtService svc = new JwtService(SECRET, TTL_SECONDS);
        UUID userId = UUID.randomUUID();
        String username = "tester";

        String token = svc.generateToken(username, userId);
        assertNotNull(token);

        JsonNode payload = decodePayload(token);

        // subject or username
        String sub = payload.path("sub").asText(null);
        String usernameClaim = payload.path("username").asText(null);
        assertTrue((username.equals(sub)) || (username.equals(usernameClaim)), "Subject or username claim should contain the username");

        // generation uses claim key 'userid' (lowercase) -> ensure present
        assertEquals(userId.toString(), payload.path("userid").asText(null));
    }

    @Test
    void extractUsername_and_extractUserId_behaviour() {
        JwtService svc = new JwtService(SECRET, TTL_SECONDS);
        UUID userId = UUID.randomUUID();
        String username = "alice";

        String token = svc.generateToken(username, userId);

        // extractUsername uses parseClaims -> returns subject
        assertEquals(username, svc.extractUsername(token));

        // extractUserId looks for 'userId' (camelCase) while generation writes 'userid' -> expect null
        assertNull(svc.extractUserId(token));
    }

    @Test
    void isValid_true_for_valid_false_for_tampered_and_expired() throws Exception {
        JwtService svc = new JwtService(SECRET, TTL_SECONDS);
        UUID userId = UUID.randomUUID();

        String token = svc.generateToken("u", userId);
        assertTrue(svc.isValid(token));

        // tamper token
        String tampered = token.substring(0, token.length() - 1) + (token.charAt(token.length() - 1) == 'a' ? 'b' : 'a');
        assertFalse(svc.isValid(tampered));

        // expired token
        JwtService shortLived = new JwtService(SECRET, 1L);
        String shortToken = shortLived.generateToken("u", userId);
        Thread.sleep(1200);
        assertFalse(shortLived.isValid(shortToken));
    }

    @Test
    void methods_handle_invalid_input_gracefully() {
        JwtService svc = new JwtService(SECRET, TTL_SECONDS);

        // null token for isValid -> should return false (caught by method)
        assertFalse(svc.isValid(null));

        // invalid tokens should cause extractUsername/Id to throw (parse fails)
        assertThrows(Exception.class, () -> svc.extractUsername("not.a.token"));
        assertThrows(Exception.class, () -> svc.extractUserId("not.a.token"));
    }
}

