package com.binarysystempatienttracker.patienttracker.web.dto;

import java.time.LocalDate;

/**
 * Request payload for creating/updating a patient
 * @param name
 * @param dob
 * @param address
 * @param phone
 * @param gender
 */
public record PatientDto(String name, LocalDate dob, String address, String phone, String gender) {
}
