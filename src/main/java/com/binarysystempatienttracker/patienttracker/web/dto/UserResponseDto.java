package com.binarysystempatienttracker.patienttracker.web.dto;

/**
 * Minimal representation of a user account for API responses
 *
 * @param id
 * @param email
 * @param role
 * @param status
 * @param doctorId
 * @param patientId
 */
public record UserResponseDto(
        Long id,
        String email,
        String role,
        String status,
        Long doctorId,
        Long patientId) {
}
