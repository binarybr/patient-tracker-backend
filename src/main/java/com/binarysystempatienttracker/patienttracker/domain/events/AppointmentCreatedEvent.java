package com.binarysystempatienttracker.patienttracker.domain.events;

import java.time.LocalDateTime;

public record AppointmentCreatedEvent(
        Long appointmentId,
        Long doctorId,
        Long patientId,
        LocalDateTime apptTime
) {}
