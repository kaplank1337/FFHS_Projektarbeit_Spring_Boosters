package ch.ffhs.authentification_service.security;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Order(-100) // Früh ausführen
public class JwtAuthenticationFilter implements WebFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        var path = exchange.getRequest().getPath().value();
        System.out.println("[JWT] Incoming request: " + exchange.getRequest().getMethod() + " " + path);
        // Öffentliche Endpunkte überspringen
        if (path.equals("/api/v1/auth/login") || path.equals("/api/v1/auth/register")) {
            System.out.println("[JWT] Public endpoint – skipping token check");
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("[JWT] Missing or invalid Authorization header");
            return unauthorized(exchange); // Kein Token vorhanden
        }

        String token = authHeader.substring(7);
        if (!jwtService.isValid(token)) {
            System.out.println("[JWT] Token invalid");
            return unauthorized(exchange); // Ungültiger Token
        }

        String username = jwtService.extractUsername(token);
        System.out.println("[JWT] Token valid for user=" + username);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                username,
                null,
                List.of(new SimpleGrantedAuthority("USER"))
        );
        SecurityContextImpl context = new SecurityContextImpl(authentication);

        // SecurityContext VOR der weiteren Verarbeitung bereitstellen
        return Mono.defer(() -> chain.filter(exchange))
                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)));
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
}
