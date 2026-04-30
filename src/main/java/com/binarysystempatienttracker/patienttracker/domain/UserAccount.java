package com.binarysystempatienttracker.patienttracker.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Application user account. May be associated with a Doctor and/or Patient profile
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class UserAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    /**
     * Role names are used as spring security roles(e.g., ADMIN/DOCTOR/PATIENT)
     */
    @Column(nullable = false)
    private String role; // ADMIN/DOCTOR/PATIENT

    /**
     * ACITVE/LOCKED; Combined with "locked" flag in legacy flows
     */
    @Builder.Default
    @Column(nullable = false)
    private String status = "ACTIVE"; // ACTIVE/LOCKED

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "password_changed_at")
    private LocalDateTime passwordChangeAt;

    @Builder.Default
    @Column(name = "locked", nullable = false)
    private boolean locked = false;

    @PrePersist
    void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public boolean isEnabled() {
        return !locked && "ACTIVE".equalsIgnoreCase(status);
    }


}
