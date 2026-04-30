package com.binarysystempatienttracker.patienttracker.web.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request payload for creating/updating a doctor
 *
 * @param name
 * @param speciality
 * @param hospital
 * @param address
 * @param phone
 * @param approved
 */
public record DoctorDto(@NotBlank String name, String speciality, String hospital, String address, String phone,
                        String gender,
                        boolean approved) {
}
