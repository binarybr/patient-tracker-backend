package com.binarysystempatienttracker.patienttracker.web.controller;

import com.binarysystempatienttracker.patienttracker.domain.Patient;
import com.binarysystempatienttracker.patienttracker.repository.PatientRepository;
import com.binarysystempatienttracker.patienttracker.repository.UserRepository;
import com.binarysystempatienttracker.patienttracker.web.dto.PatientDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Admin / Doctor CRUD for patients (soft-delete aware)
 * Patient self-service update on own profile
 */
@AllArgsConstructor
@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientRepository repo;
    private final UserRepository users;

    // ---------------------------
    // Admin / Doctor READ
    // ---------------------------

    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @GetMapping
    public List<Patient> all() {
        return repo.findByDeletedFalse();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @GetMapping(params = {"page", "size"})
    public Page<Patient> allPaged(@RequestParam int page, @RequestParam int size) {
        return repo.findByDeletedFalse(PageRequest.of(page, size));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @GetMapping("/{id}")
    public ResponseEntity<Patient> one(@PathVariable Long id) {
        return repo.findByIdAndDeletedFalse(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ---------------------------
    // Admin CREATE / UPDATE
    // ---------------------------

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Patient> create(@Valid @RequestBody PatientDto dto) {
        Patient p = Patient.builder()
                .name(dto.name())
                .dob(dto.dob())
                .address(dto.address())
                .phone(dto.phone())
                .gender(dto.gender())
                .build();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(repo.save(p));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Patient> update(
            @PathVariable Long id,
            @Valid @RequestBody PatientDto dto
    ) {
        return repo.findByIdAndDeletedFalse(id)
                .map(p -> {
                    p.setName(dto.name());
                    p.setDob(dto.dob());
                    p.setAddress(dto.address());
                    p.setPhone(dto.phone());
                    p.setGender(dto.gender());
                    return ResponseEntity.ok(repo.save(p));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ❌ HARD DELETE REMOVED FROM HERE
    // ✅ Delete logic lives ONLY in AdminController
    // DELETE /api/admin/patients/{id}?soft=true|false

    // ---------------------------
    // Patient Self-Service
    // ---------------------------

    @PreAuthorize("hasRole('PATIENT')")
    @PatchMapping("/me")
    public ResponseEntity<Patient> updateSelf(
            @AuthenticationPrincipal UserDetails ud,
            @RequestBody PatientDto dto
    ) {
        var u = users.findByEmail(ud.getUsername())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        Patient p = repo.findByIdAndDeletedFalse(u.getPatient().getId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "Patient profile is inactive"
                        ));

        if (dto.phone() != null) p.setPhone(dto.phone());
        if (dto.dob() != null) p.setDob(dto.dob());
        if (dto.gender() != null) p.setGender(dto.gender());
        if (dto.address() != null) p.setAddress(dto.address());

        return ResponseEntity.ok(repo.save(p));
    }
}