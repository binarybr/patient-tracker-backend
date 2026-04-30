package com.binarysystempatienttracker.patienttracker.web.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Admin form for creating/updating users
 *
 * @param role
 * @param status
 * @param doctorId
 * @param patientId
 */
public record UpdateUserRequestDto(@NotBlank String role, @NotBlank String status, Long doctorId, Long patientId) {
}
