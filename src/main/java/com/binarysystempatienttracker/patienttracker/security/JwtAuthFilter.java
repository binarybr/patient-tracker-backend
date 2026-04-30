package com.binarysystempatienttracker.patienttracker.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Servlet filter that authenticates requests carrying a valid Bearer JWT
 * It sets an authenticated {@link org.springframework.security.core.Authentication}
 * in the {@link org.springframework.security.core.context.SecurityContextHolder} when a valid token is found
 */

@AllArgsConstructor
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService uds;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer")) {
            String token = header.substring(7);
            if (jwtService.isValid(token)) {
                String username = jwtService.extractSubject(token);
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails ud = uds.loadUserByUsername(username);
                    var auth = new UsernamePasswordAuthenticationToken(ud, null, ud.getAuthorities());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
