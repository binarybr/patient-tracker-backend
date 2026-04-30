package com.binarysystempatienttracker.patienttracker.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Self-registration payload
 *
 * @param email
 * @param password
 * @param role
 */
public record RegisterRequestDto(@Email @NotBlank String email, @NotBlank @Size(min = 8) String password,
                                 @NotBlank String role) {
}
