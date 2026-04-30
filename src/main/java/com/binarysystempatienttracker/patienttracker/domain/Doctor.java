package com.binarysystempatienttracker.patienttracker.domain;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Doctor master profile. Soft-delete and approval flags allow moderation without data loss
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "doctors")
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
    private String speciality;
    private String hospital;
    private String address;
    private String phone;
    private String gender;

    /**
     * Soft-delete flag to retain historical references
     */
    @Builder.Default
    @Column(nullable = false)
    private boolean approved = false;

    @Builder.Default
    @Column(nullable = false)
    private boolean deleted = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

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
