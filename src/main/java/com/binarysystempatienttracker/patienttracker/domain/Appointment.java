package com.binarysystempatienttracker.patienttracker.domain;

import com.binarysystempatienttracker.patienttracker.domain.enums.AppointmentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Represents a scheduled meeting between a patient and a doctor for a given medical case
 * The entity uses audit timestamps managed via JPA lifecycle callbacks
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "appointments")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Owning medical case (diagnosis context for the appointment)
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "case_id")
    private MedicalCase medicalCase;

    /**
     * The patient attending the appointment
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    /**
     * The doctor conducting the appointment
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    /**
     * Date/time of the appointments in server timezone
     */
    @Column(name = "appt_time", nullable = false)
    private LocalDateTime apptTime;

    /**
     * Status e.g., SCHEDULED/COMPLETED/CANCELLED
     */

    @Column(nullable = false)
//    private String status = "SCHEDULED";
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private AppointmentStatus status = AppointmentStatus.SCHEDULED;


    /**
     * Creation timestamp
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * Last update timestamp
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;



    @PrePersist
    void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
