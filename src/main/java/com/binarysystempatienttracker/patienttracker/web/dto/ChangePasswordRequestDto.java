package com.binarysystempatienttracker.patienttracker.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request body for changing password of the current user
 *
 * @param oldPassword
 * @param newPassword
 */
public record ChangePasswordRequestDto(@NotBlank String oldPassword, @NotBlank @Size(min = 8) String newPassword) {
}
