package com.binarysystempatienttracker.patienttracker.repository;

import com.binarysystempatienttracker.patienttracker.domain.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for {@link Doctor}
 */
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
}
