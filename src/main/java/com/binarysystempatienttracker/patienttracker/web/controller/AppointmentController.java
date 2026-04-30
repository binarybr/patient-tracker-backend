package com.binarysystempatienttracker.patienttracker.web.controller;

import com.binarysystempatienttracker.patienttracker.domain.Appointment;
import com.binarysystempatienttracker.patienttracker.domain.UserAccount;
import com.binarysystempatienttracker.patienttracker.domain.enums.AppointmentStatus;
import com.binarysystempatienttracker.patienttracker.repository.*;
import com.binarysystempatienttracker.patienttracker.web.dto.AppointmentDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Appointment queries and creation endpoints
 * patient role has access to own appointments only via a dedicated endpoint
 */
@RestController
@RequestMapping("/api/appointments")
@AllArgsConstructor
public class AppointmentController {
    private final AppointmentRepository aptts;
    private final MedicalCaseRepository cases;
    private final PatientRepository patients;
    private final DoctorRepository doctors;
    private final UserRepository users;

    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @GetMapping
    public List<Appointment> all() {
        return aptts.findAll();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    @GetMapping("/{id}")
    public ResponseEntity<Appointment> one(@PathVariable Long id) {
        return aptts.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity
                        .notFound()
                        .build());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @GetMapping(params = "doctorId")
    public List<Appointment> byDoctor(@RequestParam Long doctorId) {
        return aptts.findByDoctor_Id(doctorId);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    @GetMapping(params = "patientId")
    public List<Appointment> byPatient(@RequestParam Long patientId) {
        return aptts.findByPatient_Id(patientId);
    }

    /**
     * Returns the current patient's appointments filtered by time range
     *
     * @param ud
     * @param from
     * @param to
     * @return
     */
    /*@PreAuthorize("hasRole('PATIENT')")
    @GetMapping("/my")
    public List<Appointment> myAppointments(
            @AuthenticationPrincipal UserDetails ud,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        var u = users.findByEmail(ud.getUsername()).orElseThrow();
        Long patientId = u.getPatient().getId();
        return aptts.findByPatient_Id(patientId).stream()
                .filter(a -> !a.getApptTime().isBefore(from) && !a.getApptTime().isAfter(to))
                .toList();
    }*/
    @PreAuthorize("hasRole('PATIENT')")
    @GetMapping("/my")
    public ResponseEntity<?> myAppointments(
            @AuthenticationPrincipal UserDetails ud,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        UserAccount u = users.findByEmail(ud.getUsername())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "User not found"
                ));

        if (u.getPatient() == null) {
            // ✅ Clear, correct API behavior
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("User is not linked to a patient profile");
        }

        Long patientId = u.getPatient().getId();

        List<Appointment> result = aptts.findByPatient_Id(patientId)
                .stream()
                .filter(a ->
                        !a.getApptTime().isBefore(from) &&
                                !a.getApptTime().isAfter(to)
                )
                .toList();

        return ResponseEntity.ok(result);
    }


    @PreAuthorize("hasRole('DOCTOR')")
    @GetMapping("/my-doctor")
    public ResponseEntity<?> myDoctorAppointments(
            @AuthenticationPrincipal UserDetails ud,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        UserAccount u = users.findByEmail(ud.getUsername())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "User not found"
                ));

        if (u.getDoctor() == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("User is not linked to a doctor profile");
        }

        Long doctorId = u.getDoctor().getId();

        List<Appointment> result = aptts.findByDoctor_Id(doctorId)
                .stream()
                .filter(a ->
                        !a.getApptTime().isBefore(from) &&
                                !a.getApptTime().isAfter(to)
                )
                .toList();

        return ResponseEntity.ok(result);
    }

    /**
     * Creates a new appointment, validating references by ID
     *
     * @param dto
     * @return
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody AppointmentDto dto) {

        var mc = cases.findById(dto.caseId()).orElse(null);
        if (mc == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unknown caseId");

        var p = patients.findById(dto.patientId()).orElse(null);
        if (p == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unknown patientId");

        var d = doctors.findById(dto.doctorId()).orElse(null);
        if (d == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unknown doctorId");

        var appt = Appointment.builder()
                .medicalCase(mc)
                .patient(p)
                .doctor(d)
                .apptTime(dto.apptTime())
                .status(
                        dto.status() != null
                                ? AppointmentStatus.valueOf(dto.status())
                                : AppointmentStatus.SCHEDULED
                )
                .build();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(aptts.save(appt));
    }

}
