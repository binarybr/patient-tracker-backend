package com.binarysystempatienttracker.patienttracker.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Stored (hashed) refresh tokens to support rotation & revocation
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Subject (user email) this token belongs to
     */
    @Column(nullable = false)
    private String subject; // User email

    /**
     * Hash of the refresh token
     */
    @Column(name = "token_hash", nullable = false)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    /**
     * If true, token cannot be used anymore
     */
    @Builder.Default
    @Column(nullable = false)
    private boolean revoked = false;

    /**
     * Hash of the newly issued token if this one was rotated
     */
    @Column(name = "replaced_by")
    private String replacedBy;

    /**
     * Optional client info for auditing; not used for auth decisions
     */
    @Column(name = "user_agent")
    private String userAgent;
    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

}
