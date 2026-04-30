package com.binarysystempatienttracker.patienttracker.repository;

import com.binarysystempatienttracker.patienttracker.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for refresh tokens; used to validate/rotate tokens
 */
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);
}
