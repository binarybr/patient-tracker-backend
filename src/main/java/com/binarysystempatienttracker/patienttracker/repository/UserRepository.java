package com.binarysystempatienttracker.patienttracker.repository;

import com.binarysystempatienttracker.patienttracker.domain.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for {@link UserAccount}
 */
public interface UserRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByEmail(String email);
}
