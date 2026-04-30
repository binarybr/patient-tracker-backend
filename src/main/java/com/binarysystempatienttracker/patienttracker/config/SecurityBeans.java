package com.binarysystempatienttracker.patienttracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Central place for security-related beans that are not tied to the Spring Security filter chain configuration
 */

@Configuration
public class SecurityBeans {

    /**
     * Password encoder used throughout the app for hashing user passwords.
     * BCrypt is intentionally chosen for its adaptive strength against brute-force attacks.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
