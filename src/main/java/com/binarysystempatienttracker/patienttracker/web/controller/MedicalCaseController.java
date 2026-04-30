package com.binarysystempatienttracker.patienttracker.web.controller;

import com.binarysystempatienttracker.patienttracker.domain.MedicalCase;
import com.binarysystempatienttracker.patienttracker.repository.DoctorRepository;
import com.binarysystempatienttracker.patienttracker.repository.MedicalCaseRepository;
import com.binarysystempatienttracker.patienttracker.repository.PatientRepository;
import com.binarysystempatienttracker.patienttracker.web.dto.CaseDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CRUD endpoints for medical cases plus a status update operation
 */

@RestController
@RequestMapping("/api/cases")
@AllArgsConstructor
public class MedicalCaseController {
    private final MedicalCaseRepository repo;
    private final PatientRepository patients;
    private final DoctorRepository doctors;

    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @GetMapping
    public List<MedicalCase> all() {
        return repo.findAll();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @GetMapping("/{id}")
    public ResponseEntity<MedicalCase> one(Long id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CaseDto dto){
        var patient = patients.findById(dto.patientId()).orElse(null);
        if (patient==null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unknown patientId");
        var doctor = doctors.findById(dto.doctorId()).orElse(null);
        if (doctor==null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unknown doctorId");
        var mc=MedicalCase.builder()
                .patient(patient)
                .doctor(doctor)
                .title(dto.title())
                .diagnosis(dto.diagnosis())
                .symptoms(dto.symptoms())
                .medicines(dto.medicines())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(mc));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> setStatus(@PathVariable Long id, @RequestParam String status){
        return repo.findById(id).map(c->{
            c.setStatus(status);
            return ResponseEntity.ok(repo.save(c));
        }).orElse(ResponseEntity.notFound().build());
    }
}

