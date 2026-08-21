package com.shopsphere.api_gateway;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpMethod; // Import zaroori hai
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtFilter implements WebFilter {
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    /**
     * Constructor injection for JwtUtil (to validate tokens) 
     * and ObjectMapper (to convert Java objects to JSON).
     */
    public JwtFilter(JwtUtil jwtUtil, ObjectMapper objectMapper) {
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
    }

    /**
     * Main filtering logic. This method intercepts every request.
     * It handles:
     * 1. CORS Preflight (OPTIONS) requests
     * 2. Bypassing security for public endpoints (login/signup)
     * 3. Validating JWT tokens for secured endpoints
     * 4. Injecting user details into request headers for internal services
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // 1. Bypass Preflight (OPTIONS) requests. 
        // Browsers send this without tokens to check CORS permissions.
        if (exchange.getRequest().getMethod() == HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }

        String path = exchange.getRequest().getURI().getPath();

        // 2. Skip authentication for public endpoints (login and signup).
        // New users or logging-in users don't have tokens yet.
        if (path.contains("/auth/signup") || path.contains("/auth/login")) {
            return chain.filter(exchange);
        }

        // Extract Authorization header
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        // Validate header presence and format (must start with "Bearer ")
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return sendUnauthorizedResponse(exchange, "Authorization header missing");
        }

        String token = authHeader.substring(7);

        // Validate token integrity and expiration
        if (!jwtUtil.validateToken(token)) {
            return sendUnauthorizedResponse(exchange, "Invalid or expired token");
        }

        // 3. Extract identity details (email and role) from the valid token.
        String email = jwtUtil.extractEmail(token);
        String role = jwtUtil.extractRole(token);

        // 4. Mutation: Since the request is immutable, we create a copy (mutate)
        // and inject custom headers (X-User-Email, X-User-Role) for downstream services.
        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(exchange.getRequest().mutate()
                        .header("X-User-Email", email)
                        .header("X-User-Role", role)
                        .build())
                .build();

        // Pass the modified request to the next filter in the chain.
        return chain.filter(mutatedExchange);
    }

    /**
     * Helper method to send a custom 401 Unauthorized JSON response.
     * This is used when a token is missing, invalid, or expired.
     */
    private Mono<Void> sendUnauthorizedResponse(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        // Create a structured error response map
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("status", 401);
        errorResponse.put("error", "Unauthorized");
        errorResponse.put("message", message);
        errorResponse.put("path", exchange.getRequest().getURI().getPath());

        try {
            // Convert Map to JSON byte array
            byte[] bytes = objectMapper.writeValueAsBytes(errorResponse);
            // Write the JSON bytes to the response body
            return exchange.getResponse().writeWith(
                    Mono.just(exchange.getResponse().bufferFactory().wrap(bytes))
            );
        } catch (JsonProcessingException e) {
            // Fallback: Just complete the response without a body if JSON conversion fails
            return exchange.getResponse().setComplete();
        }
    }
}
