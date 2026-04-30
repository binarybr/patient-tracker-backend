package com.binarysystempatienttracker.patienttracker.config;

import com.binarysystempatienttracker.patienttracker.domain.Doctor;
import com.binarysystempatienttracker.patienttracker.domain.MedicalCase;
import com.binarysystempatienttracker.patienttracker.domain.Patient;
import com.binarysystempatienttracker.patienttracker.domain.UserAccount;
import com.binarysystempatienttracker.patienttracker.repository.DoctorRepository;
import com.binarysystempatienttracker.patienttracker.repository.MedicalCaseRepository;
import com.binarysystempatienttracker.patienttracker.repository.PatientRepository;
import com.binarysystempatienttracker.patienttracker.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

/**
 * Seeds a minimal dataset for development/demo environments.
 * The logic intentionally checks counts/absence to avoid duplicating on restart.
 * No production-only assumptions are embedded; feel free to put a spring profile
 * if needed (e.g., {@code @Profile("dev")})
 */

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seed(DoctorRepository doctors,
                           PatientRepository patients,
                           MedicalCaseRepository cases,
                           UserRepository users,
                           PasswordEncoder pe) {
        return args -> {

            // -- Doctors --
            if (doctors.count() == 0) {
                doctors.save(Doctor.builder()
                        .name("Dr. Aditi Rao")
                        .speciality("Cardiology")
                        .hospital("City Hospital")
                        .address("Hinjewadi, Pune")
                        .phone("9097572567")
                        .approved(true)
                        .build());

                doctors.save(Doctor.builder()
                        .name("Dr. Nikhil Shah")
                        .speciality("Dermatology")
                        .hospital("Metro Clinic")
                        .address("Hinjewadi, Pune")
                        .phone("9097576567")
                        .approved(true)
                        .build());
            }

            // -- Patients --
            if (patients.count() == 0) {
                patients.save(Patient.builder()
                        .name("John Doe")
                        .dob(LocalDate.of(1990, 1, 5))
                        .phone("7687543257")
                        .address("Pune")
                        .gender("M")
                        .build());

                patients.save(Patient.builder()
                        .name("Jane Smith")
                        .dob(LocalDate.of(1988, 1, 5))
                        .phone("7687544567")
                        .address("Mumbai")
                        .gender("F")
                        .build());
            }

            // -- Medical Case --
            if (cases.count() == 0) {
                var p = patients.findAll().get(0);
                var d = doctors.findAll().get(0);

                cases.save(MedicalCase.builder()
                        .patient(p)
                        .doctor(d)
                        .title("General Checkup")
                        .diagnosis("Fever")
                        .symptoms("Headache")
                        .medicines("Paracetamol")
                        .status("OPEN")
                        .build());
            }

            // -- Admin user --
            if (users.findByEmail("admin@local.com").isEmpty()) {
                users.save(UserAccount.builder()
                        .email("admin@local.com")
                        .passwordHash(pe.encode("Admin@123"))
                        .role("ADMIN")
                        .status("ACTIVE")
                        .build());
            }

            // -- Patient bootstrap user --
            if (users.findByEmail("patient@local.com").isEmpty()) {
                Patient p = patients.findAll().get(0);

                users.save(UserAccount.builder()
                        .email("patient@local.com")
                        .passwordHash(pe.encode("Patient@123"))
                        .role("PATIENT")
                        .status("ACTIVE")
                        .patient(p)
                        .build());
            }

            // -- Doctor bootstrap user --
            if (users.findByEmail("doctor@local.com").isEmpty()) {
                Doctor d = doctors.findAll().get(0);

                users.save(UserAccount.builder()
                        .email("doctor@local.com")
                        .passwordHash(pe.encode("Doctor@123"))
                        .role("DOCTOR")
                        .status("ACTIVE")
                        .doctor(d)
                        .build());
            }
        };
    }
}

