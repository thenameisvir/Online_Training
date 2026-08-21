package com.shopsphere.api_gateway.config;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.LinkedHashSet;
import java.util.List;
//Bodyguard of CorsConfig
@Component
public class DedupeCorsResponseHeaderFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            HttpHeaders headers = exchange.getResponse().getHeaders();
            dedupe(headers, HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, true);
            dedupe(headers, HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, true);
            dedupe(headers, HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, false);
            dedupe(headers, HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, false);
        }));
    }

    private static void dedupe(HttpHeaders headers, String headerName, boolean singleValue) {
        List<String> values = headers.get(headerName);
        if (values == null || values.isEmpty()) return;

        LinkedHashSet<String> uniq = new LinkedHashSet<>();
        for (String v : values) {
            if (v == null) continue;
            // Spring may store duplicates either as multiple header entries,
            // or as a single comma-separated value: "a, a"
            for (String part : v.split(",")) {
                String trimmed = part.trim();
                if (!trimmed.isEmpty()) uniq.add(trimmed);
            }
        }

        if (uniq.isEmpty()) return;

        if (singleValue) {
            headers.set(headerName, uniq.iterator().next());
        } else {
            headers.set(headerName, String.join(", ", uniq));
        }
    }

    @Override
    public int getOrder() {
        // Run late so it can fix headers added by others
        return Ordered.LOWEST_PRECEDENCE;
    }
}

