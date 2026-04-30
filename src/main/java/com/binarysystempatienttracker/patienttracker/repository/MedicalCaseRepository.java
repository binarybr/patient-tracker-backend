package com.binarysystempatienttracker.patienttracker.repository;

import com.binarysystempatienttracker.patienttracker.domain.MedicalCase;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for {@link MedicalCase}
 */
public interface MedicalCaseRepository extends JpaRepository<MedicalCase, Long> {
}
