package ch.ffhs.authentification_service.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class JwtResponseFilter implements GlobalFilter, Ordered {

    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    public JwtResponseFilter(JwtService jwtService) {
        this.jwtService = jwtService;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        HttpMethod method = exchange.getRequest().getMethod();

        // Nur bei POST /api/v1/auth/login oder /api/v1/auth/register
        if ((path.equals("/api/v1/auth/login") || path.equals("/api/v1/auth/register"))
                && HttpMethod.POST.equals(method)) {

            ServerHttpResponse originalResponse = exchange.getResponse();
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();

            ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                @Override
                public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                    if (body instanceof Flux) {
                        Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;
                        return super.writeWith(fluxBody.buffer().map(dataBuffers -> {
                            DataBuffer joinedBuffer = bufferFactory.join(dataBuffers);
                            byte[] content = new byte[joinedBuffer.readableByteCount()];
                            joinedBuffer.read(content);
                            DataBufferUtils.release(joinedBuffer);

                            try {
                                String responseBody = new String(content, StandardCharsets.UTF_8);
                                JsonNode jsonNode = objectMapper.readTree(responseBody);

                                // Username extrahieren
                                String username = null;
                                if (jsonNode.has("userName")) {
                                    username = jsonNode.get("userName").asText();
                                } else if (jsonNode.has("username")) {
                                    username = jsonNode.get("username").asText();
                                }

                                if (username != null) {
                                    // JWT Token generieren
                                    String token = jwtService.generateToken(username);

                                    // Token zum Response hinzufügen
                                    ((ObjectNode) jsonNode).put("token", token);

                                    String modifiedResponse = objectMapper.writeValueAsString(jsonNode);
                                    byte[] modifiedBytes = modifiedResponse.getBytes(StandardCharsets.UTF_8);

                                    return bufferFactory.wrap(modifiedBytes);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            return bufferFactory.wrap(content);
                        }));
                    }
                    return super.writeWith(body);
                }
            };

            return chain.filter(exchange.mutate().response(decoratedResponse).build());
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1; // Hohe Priorität
    }
}

