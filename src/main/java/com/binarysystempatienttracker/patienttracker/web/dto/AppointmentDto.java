package com.binarysystempatienttracker.patienttracker.web.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AppointmentDto(@NotNull Long caseId, @NotNull Long patientId, @NotNull Long doctorId,
                             @NotNull LocalDateTime apptTime, String status) {
}
