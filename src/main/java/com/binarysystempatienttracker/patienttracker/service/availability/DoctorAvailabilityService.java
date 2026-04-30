package com.binarysystempatienttracker.patienttracker.service.availability;

import com.binarysystempatienttracker.patienttracker.domain.availability.DoctorAvailability;
import com.binarysystempatienttracker.patienttracker.repository.DoctorAvailabilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorAvailabilityService {

    private final DoctorAvailabilityRepository repo;

    public List<DoctorAvailability> getDoctorAvailability(Long doctorId) {
        return repo.findByDoctor_IdAndActiveTrue(doctorId);
    }

    public DoctorAvailability save(DoctorAvailability a) {
        return repo.save(a);
    }
}