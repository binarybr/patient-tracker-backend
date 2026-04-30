package com.binarysystempatienttracker.patienttracker.web.controller;

import com.binarysystempatienttracker.patienttracker.domain.Doctor;
import com.binarysystempatienttracker.patienttracker.domain.Patient;
import com.binarysystempatienttracker.patienttracker.repository.DoctorRepository;
import com.binarysystempatienttracker.patienttracker.repository.PatientRepository;
import com.binarysystempatienttracker.patienttracker.repository.UserRepository;
import com.binarysystempatienttracker.patienttracker.web.dto.UserResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Set;

/**
 * Admin-only endpoints for user/doctor/patient administrative actions
 */
@AllArgsConstructor
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserRepository users;
    private final DoctorRepository doctors;
    private final PatientRepository patients;

    // Optional: restrict allowed statuses to avoid arbitrary values from UI
    private static final Set<String> ALLOWED_USER_STATUSES =
            Set.of("ACTIVE", "LOCKED", "BLOCKED", "SUSPENDED");

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/users/{id}/status")
    public ResponseEntity<UserResponseDto> setUserStatus(
            @PathVariable Long id,
            @RequestParam String status
    ) {
        var u = users.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        String normalized = status == null ? "" : status.trim().toUpperCase();

        // If you don't want validation, remove this if-block.
        if (!ALLOWED_USER_STATUSES.contains(normalized)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid status. Allowed: " + ALLOWED_USER_STATUSES
            );
        }

        u.setStatus(normalized);
        users.save(u);

        return ResponseEntity.ok(new UserResponseDto(
                u.getId(), u.getEmail(), u.getRole(), u.getStatus(),
                u.getDoctor() != null ? u.getDoctor().getId() : null,
                u.getPatient() != null ? u.getPatient().getId() : null
        ));
    }

    // -------------------- DOCTOR ADMIN --------------------

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/doctors/{id}/approve")
    public ResponseEntity<Doctor> approveDoctor(@PathVariable Long id) {
        Doctor d = doctors.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not found"));

        if (d.isDeleted()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot approve a deleted doctor. Restore first."
            );
        }

        d.setApproved(true);
        return ResponseEntity.ok(doctors.save(d));
    }

    // Optional but useful: revoke approval
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/doctors/{id}/reject")
    public ResponseEntity<Doctor> rejectDoctor(@PathVariable Long id) {
        Doctor d = doctors.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not found"));

        // Reject means: not approved (and usually not deleted)
        d.setApproved(false);
        return ResponseEntity.ok(doctors.save(d));
    }

    /**
     * Soft or hard delete of a doctor record
     * soft=true -> sets deleted=true, approved=false
     * soft=false -> hard delete row (use with caution)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/doctors/{id}")
    public ResponseEntity<Void> deleteDoctor(
            @PathVariable Long id,
            @RequestParam(defaultValue = "true") boolean soft
    ) {
        Doctor d = doctors.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not found"));

        if (soft) {
            // Soft delete
            d.setDeleted(true);
            d.setApproved(false); // safety: inactive doctor can’t be used
            doctors.save(d);
        } else {
            // Hard delete (irreversible)
            doctors.deleteById(id);
        }

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/doctors/{id}/restore")
    public ResponseEntity<Doctor> restoreDoctor(@PathVariable Long id) {
        Doctor d = doctors.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not found"));

        d.setDeleted(false);
        return ResponseEntity.ok(doctors.save(d));
    }

    // -------------------- PATIENT ADMIN --------------------

    /**
     * Soft or hard delete of a patient record
     * soft=true -> sets deleted=true
     * soft=false -> hard delete row (use with caution)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/patients/{id}")
    public ResponseEntity<Void> deletePatient(
            @PathVariable Long id,
            @RequestParam(defaultValue = "true") boolean soft
    ) {
        Patient p = patients.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));

        if (soft) {
            p.setDeleted(true);
            patients.save(p);
        } else {
            patients.deleteById(id);
        }
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/patients/{id}/restore")
    public ResponseEntity<Patient> restorePatient(@PathVariable Long id) {
        Patient p = patients.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));

        p.setDeleted(false);
        return ResponseEntity.ok(patients.save(p));
    }

    // -------------------- STATS --------------------

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/stats")
    public Map<String, Object> stats() {
        return Map.of(
                "users", users.count(),
                "doctors", doctors.count(),
                "patients", patients.count()
        );
    }
}