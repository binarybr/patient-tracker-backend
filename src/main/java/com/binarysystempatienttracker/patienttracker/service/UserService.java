package com.binarysystempatienttracker.patienttracker.service;

import com.binarysystempatienttracker.patienttracker.domain.UserAccount;
import com.binarysystempatienttracker.patienttracker.repository.DoctorRepository;
import com.binarysystempatienttracker.patienttracker.repository.PatientRepository;
import com.binarysystempatienttracker.patienttracker.repository.UserRepository;
import com.binarysystempatienttracker.patienttracker.web.dto.UpdateUserRequestDto;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Admin-facing operations on {@link UserAccount} and linking with doctor/patient profiles
 */

@AllArgsConstructor
@Service
public class UserService {
    private final UserRepository users;
    private final DoctorRepository doctors;
    private final PatientRepository patients;
    private final PasswordEncoder pe;


    public List<UserAccount> list() {
        return users.findAll();
    }

    public UserAccount get(Long id) {
        return users.findById(id).orElseThrow();
    }

    /**
     * Creates a placeholder user with a temporary password until onboarding completes
     *
     * @param req
     * @return
     */
    public UserAccount create(UpdateUserRequestDto req) {
        UserAccount u = UserAccount.builder()
                .email("pending-" + System.nanoTime() + "@local")
                .passwordHash(pe.encode("ChangeMe@123"))
                .role(req.role().toUpperCase())
                .status(req.status().toUpperCase())
                .build();
        if (req.doctorId() != null) u.setDoctor(doctors.findById(req.doctorId()).orElseThrow());
        if (req.patientId() != null) u.setPatient(patients.findById(req.patientId()).orElseThrow());
        return users.save(u);
    }

    /**
     * Updates role/status and associations
     *
     * @param id
     * @param req
     * @return
     */
    public UserAccount update(Long id, UpdateUserRequestDto req) {
        UserAccount u = get(id);
        u.setRole(req.role().toUpperCase());
        u.setStatus(req.status().toUpperCase());
        if (req.doctorId() != null) u.setDoctor(doctors.findById(req.doctorId()).orElseThrow());
        else u.setDoctor(null);
        if (req.patientId() != null) u.setPatient(patients.findById(req.patientId()).orElseThrow());
        else u.setPatient(null);
        return users.save(u);
    }

    public void delete(Long id) {
        users.deleteById(id);
    }

    /**
     * Changes the current user's password after validating the old one
     *
     * @param u
     * @param oldPw
     * @param newPw
     */
    @Transactional
    public void changePassword(UserAccount u, String oldPw, String newPw) {
        if (!pe.matches(oldPw, u.getPasswordHash())) throw new IllegalArgumentException("Old password mismatch");
        u.setPasswordHash(pe.encode(newPw));
        users.save(u);
    }

    /**
     * Links an account to doctor/patient profiles (idempotent for null inputs)
     *
     * @param u
     * @param doctorId
     * @param patientId
     * @return
     */
    @Transactional
    public UserAccount linkProfile(UserAccount u, Long doctorId, Long patientId) {
        if (doctorId != null) u.setDoctor(doctors.findById(doctorId).orElseThrow());
        if (patientId != null) u.setPatient(patients.findById(patientId).orElseThrow());
        return users.save(u);
    }

}
