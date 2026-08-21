package com.shopsphere.catalogservice.security;

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
import java.util.Collections;
import java.util.List;

@Component
public class RoleHeaderFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {


        /*
         * 1. Extract Identity Headers
         * Getting the Role and Email sent by the API Gateway
         */

        String role = request.getHeader("X-User-Role");
        String email = request.getHeader("X-User-Email");

        /*
         * 2. Check if the request is coming from a logged-in user
         * If headers exist, it means the Gateway has already verified the JWT
         */
        if (role != null && email != null) {
            System.out.println("DEBUG: User Role from Header: " + role);

            /*
             * 3. Convert Role to Spring Authority
             * Spring expects roles to start with "ROLE_".
             * Example: ADMIN becomes ROLE_ADMIN
             */

            SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role.toUpperCase());
            List<SimpleGrantedAuthority> authorities = Collections.singletonList(authority);
            /*
             * 4. Create an Authentication Object
             * This object tells Spring: "This is the user's email, and these are his permissions."
             */



            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(email, null, authorities);


            /*
             * 5. Set Identity in Security Context
             * We are officially telling Spring Security that this user is VALID.
             * Now @PreAuthorize("hasRole('ADMIN')") will be able to read this.
             */
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        /*
         * 6. Continue the Chain
         * Passing the request to the next filter or the Controller
         */

        filterChain.doFilter(request, response);
    }
}