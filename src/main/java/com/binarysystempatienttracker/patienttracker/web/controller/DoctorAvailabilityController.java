package com.binarysystempatienttracker.patienttracker.web.controller;

import com.binarysystempatienttracker.patienttracker.domain.Doctor;
import com.binarysystempatienttracker.patienttracker.domain.availability.DoctorAvailability;
import com.binarysystempatienttracker.patienttracker.service.availability.DoctorAvailabilityService;
import com.binarysystempatienttracker.patienttracker.service.guard.UserProfileGuard;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctor-availability")
@PreAuthorize("hasRole('DOCTOR')")
@RequiredArgsConstructor
public class DoctorAvailabilityController {

    private final DoctorAvailabilityService service;
    private final UserProfileGuard guard;

    @GetMapping("/my")
    public List<DoctorAvailability> myAvailability(
            @AuthenticationPrincipal UserDetails ud) {

        Doctor d = guard.requireDoctor(guard.loadUser(ud));
        return service.getDoctorAvailability(d.getId());
    }

    @PostMapping
    public DoctorAvailability create(
            @AuthenticationPrincipal UserDetails ud,
            @RequestBody DoctorAvailability a) {

        Doctor d = guard.requireDoctor(guard.loadUser(ud));
        a.setDoctor(d);
        return service.save(a);
    }
}
