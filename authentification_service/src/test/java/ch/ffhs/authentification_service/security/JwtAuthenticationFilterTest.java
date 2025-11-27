package ch.ffhs.authentification_service.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    private JwtValidator jwtValidator;
    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        jwtValidator = mock(JwtValidator.class);
        filter = new JwtAuthenticationFilter(jwtValidator);
    }

    @Test
    void filter_skips_public_paths() {
        MockServerHttpRequest req = MockServerHttpRequest.get("/api/v1/auth/login").build();
        ServerWebExchange exchange = MockServerWebExchange.from(req);

        WebFilterChain mockChain = (exchange1) -> Mono.empty();
        Mono<Void> result = filter.filter(exchange, mockChain);


        result.block();
    }

    @Test
    void filter_returns_unauthorized_if_no_header() {
        MockServerHttpRequest req = MockServerHttpRequest.get("/api/v1/secure").build();
        ServerWebExchange exchange = MockServerWebExchange.from(req);

        WebFilterChain mockChain = (exchange1) -> Mono.empty();
        Mono<Void> result = filter.filter(exchange, mockChain);

        result.block();

        assertEquals(401, exchange.getResponse().getStatusCode().value());
    }

    @Test
    void filter_returns_unauthorized_if_token_invalid() {
        String token = "Bearer badtoken";
        MockServerHttpRequest req = MockServerHttpRequest.get("/api/v1/secure").header(HttpHeaders.AUTHORIZATION, token).build();
        ServerWebExchange exchange = MockServerWebExchange.from(req);

        when(jwtValidator.isValid("badtoken")).thenReturn(false);

        WebFilterChain mockChain = (exchange1) -> Mono.empty();
        Mono<Void> result = filter.filter(exchange, mockChain);
        result.block();
        assertEquals(401, exchange.getResponse().getStatusCode().value());
    }
}
