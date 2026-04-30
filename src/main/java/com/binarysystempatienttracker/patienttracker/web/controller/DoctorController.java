package com.binarysystempatienttracker.patienttracker.web.controller;

import com.binarysystempatienttracker.patienttracker.domain.Doctor;
import com.binarysystempatienttracker.patienttracker.repository.DoctorRepository;
import com.binarysystempatienttracker.patienttracker.web.dto.DoctorDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CRUD endpoints for Doctor entities
 */

@RestController
@RequestMapping("/api/doctors")
@AllArgsConstructor
public class DoctorController {
    private final DoctorRepository repo;

    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @GetMapping
    public List<Doctor> all() {
        return repo.findAll();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @GetMapping("/{id}")
    public ResponseEntity<Doctor> one(@PathVariable Long id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity
                        .notFound()
                        .build()
                );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Doctor> create(@Valid @RequestBody DoctorDto dto) {
        Doctor d = Doctor.builder()
                .name(dto.name())
                .speciality(dto.speciality())
                .hospital(dto.hospital())
                .address(dto.address())
                .phone(dto.phone())
                .gender(dto.gender())
                .approved(dto.approved())
                .build();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(repo.save(d));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Doctor> update(@PathVariable Long id, @Valid @RequestBody DoctorDto dto) {
        return repo.findById(id).map(d -> {
            d.setName(dto.name());
            d.setSpeciality(dto.speciality());
            d.setHospital(dto.hospital());
            d.setAddress(dto.address());
            d.setPhone(dto.phone());
            d.setGender(dto.gender());
            d.setApproved(dto.approved());
            return ResponseEntity
                    .ok(repo.save(d));
        }).orElse(ResponseEntity
                .notFound()
                .build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();

    }

}
