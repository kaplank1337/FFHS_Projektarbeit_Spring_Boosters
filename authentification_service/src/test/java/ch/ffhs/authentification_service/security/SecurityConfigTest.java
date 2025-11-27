package ch.ffhs.authentification_service.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import static org.junit.jupiter.api.Assertions.*;

class SecurityConfigTest {

    @Test
    void beans_available_and_configured() {
        SecurityConfig cfg = new SecurityConfig();
        var encoder = cfg.passwordEncoder();
        assertNotNull(encoder);

        var uds = cfg.userDetailsService(encoder);
        assertNotNull(uds);

        var authManager = cfg.authManager(uds, encoder);
        assertNotNull(authManager);

        // Build a minimal ServerHttpSecurity to pass into the filter chain
        var http = ServerHttpSecurity.http();
        SecurityWebFilterChain chain = cfg.springSecurityFilterChain(http);
        assertNotNull(chain);
    }
}

