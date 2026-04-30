package com.binarysystempatienttracker.patienttracker.repository;

import com.binarysystempatienttracker.patienttracker.domain.availability.DoctorAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoctorAvailabilityRepository
        extends JpaRepository<DoctorAvailability, Long> {

    List<DoctorAvailability>
    findByDoctor_IdAndActiveTrue(Long doctorId);
}
