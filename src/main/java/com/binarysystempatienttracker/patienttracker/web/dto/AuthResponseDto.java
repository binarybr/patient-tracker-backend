package com.binarysystempatienttracker.patienttracker.web.dto;

/**
 * Response body for authentication endpoints
 *
 * @param accessToken
 * @param refreshToken
 * @param tokenType
 */
public record AuthResponseDto(String accessToken, String refreshToken, String tokenType) {
}
