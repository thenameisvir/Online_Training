package com.shopsphere.adminservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class RoleHeaderFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. CORS Preflight (OPTIONS) request ko handle karo
        // Browser pehle OPTIONS bhejta hai, usme headers nahi hote.
        // Isey bypass karna zaroori hai varna request 401/403 ho jayegi.
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        // 2. Gateway se aane wale headers uthao
        String role = request.getHeader("X-User-Role");
        String email = request.getHeader("X-User-Email");

        if (role != null && email != null) {
            // Spring Security ko batana ki ye user authenticated hai
            // Role ko "ROLE_" prefix ke saath set karna zaroori hai (e.g., ROLE_ADMIN)
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role.toUpperCase());

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(email, null, List.of(authority));

            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        // 3. Request ko aage bhej do
        filterChain.doFilter(request, response);
    }
}