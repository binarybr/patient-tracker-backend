package com.binarysystempatienttracker.patienttracker.config;

import com.binarysystempatienttracker.patienttracker.security.JwtAuthFilter;
import jakarta.servlet.Filter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Spring Security HTTP configuration.
 * Stateless sessions with JWT-based auth
 * Permits public endpoints for auth, health and Swagger.
 * Register a global CORS configuration suitable for local dev
 */

@Configuration
public class SecurityConfig {
    private final JwtAuthFilter jwtFilter;

    public SecurityConfig(JwtAuthFilter f) {
        this.jwtFilter = f;
    }

    /**
     * GLobal CORS configuration applied by Spring Security
     * Adjust allowed origins as needed (add prod/stage URLs when we deploy)
     */

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        //Allow our Vite dev server origin
        cfg.setAllowedOrigins(List.of("http://localhost:5173"));
        //HTTP methods we intend to use
        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        //Headers the browser/app will send
        cfg.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "X-Requested-With"));
        // If we rely on cookies, keep this true; for Bearer tokens it's fine either way
        cfg.setAllowCredentials(true);
        // Expose headers we want JS to read from responses (useful for file downloads)
        cfg.setExposedHeaders(List.of("Content-Disposition"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }

    /**
     * Defines the request authorization rules and filter chain
     */

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // We are stateless using JWT
                .cors(cors -> {/* use bean above */})
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Always allow preflight
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Public endpoints
                        .requestMatchers("/api/auth/**", "/api/health", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        // Others protected
                        .anyRequest().authenticated()
                )
                .addFilterBefore((Filter) jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * Exposes the {@link org.springframework.security.authentication.AuthenticationManager} from the current configuration
     * to be injected where needed (e.g., in {@code AuthService})
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }
}
