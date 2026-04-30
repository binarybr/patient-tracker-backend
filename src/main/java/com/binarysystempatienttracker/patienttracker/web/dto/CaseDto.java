package com.binarysystempatienttracker.patienttracker.web.dto;

import jakarta.validation.constraints.NotNull;

/**
 * Request payload to create a medical case
 * @param patientId
 * @param doctorId
 * @param title
 * @param diagnosis
 * @param symptoms
 * @param medicines
 */
public record CaseDto(@NotNull Long patientId, @NotNull Long doctorId, String title, String diagnosis, String symptoms,
                      String medicines) {
}
