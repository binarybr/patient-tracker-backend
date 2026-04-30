package com.binarysystempatienttracker.patienttracker.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

/**
 * Enables method-level security annotations (e.g., {@code @PreAuthorize}).
 * This allows controllers/services to declaratively guard methods based on roles
 * and expressions without changing any request mapping logic.
 */

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class MethodSecurityConfig {
}
