package com.binarysystempatienttracker.patienttracker.repository;

import com.binarysystempatienttracker.patienttracker.domain.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    // Default business reads (exclude deleted)
    List<Patient> findByDeletedFalse();

    Page<Patient> findByDeletedFalse(Pageable pageable);

    Optional<Patient> findByIdAndDeletedFalse(Long id);
}