package com.binarysystempatienttracker.patienttracker.repository;

import com.binarysystempatienttracker.patienttracker.domain.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for password reset tokens
 */
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByTokenHash(String tokenHash);
}
