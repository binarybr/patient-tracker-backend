package com.binarysystempatienttracker.patienttracker.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Global scheduling configuration for the application.
 * Enabling {@link org.springframework.scheduling.annotation.EnableScheduling} allows Spring to pick up any beans
 * with {@code @Scheduled} methods (e.g., {@code RemiderJob}).
 * There is no other logic here; this class simply flips the features on at the application context level
 */

@Configuration
@EnableScheduling
public class SchedulingConfig {
}
