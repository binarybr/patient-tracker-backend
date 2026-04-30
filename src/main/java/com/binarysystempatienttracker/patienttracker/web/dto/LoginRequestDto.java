package com.binarysystempatienttracker.patienttracker.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Credentials payload for login endpoint
 *
 * @param email
 * @param password
 */
public record LoginRequestDto(@Email @NotBlank String email, @NotBlank String password) {
}
