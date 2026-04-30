package com.binarysystempatienttracker.patienttracker.web.dto;

/**
 * Request to link current user with doctor/patient profiles
 *
 * @param doctorId
 * @param patientId
 */
public record LinkProfileRequestDto(Long doctorId, Long patientId) {
}
