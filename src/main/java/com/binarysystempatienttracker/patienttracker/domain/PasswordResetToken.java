package com.binarysystempatienttracker.patienttracker.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * One-time token (stored as a secure hash) for password reset flows
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Email address of the user requesting the reset
     */
    @Column(nullable = false)
    private String email;

    /**
     * SHA-256 hash of the raw reset token(never store raw token)
     */
    @Column(name = "token_hash", nullable = false)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    /**
     * Marked true after a successful reset to prevent replay
     */
    @Builder.Default
    @Column(nullable = false)
    private boolean used = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
