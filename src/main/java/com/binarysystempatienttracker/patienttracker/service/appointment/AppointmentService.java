package com.binarysystempatienttracker.patienttracker.service.appointment;

import com.binarysystempatienttracker.patienttracker.domain.Appointment;
import com.binarysystempatienttracker.patienttracker.domain.enums.AppointmentStatus;
import com.binarysystempatienttracker.patienttracker.domain.events.AppointmentCreatedEvent;
import com.binarysystempatienttracker.patienttracker.repository.AppointmentRepository;
import com.binarysystempatienttracker.patienttracker.repository.DoctorRepository;
import com.binarysystempatienttracker.patienttracker.repository.MedicalCaseRepository;
import com.binarysystempatienttracker.patienttracker.repository.PatientRepository;
import com.binarysystempatienttracker.patienttracker.web.dto.AppointmentDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository apptRepo;
    private final MedicalCaseRepository caseRepo;
    private final PatientRepository patientRepo;
    private final DoctorRepository doctorRepo;

    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Appointment createAppointment(AppointmentDto dto) {

        // 1) Validate references (same behavior as your controller) [2](https://onedrive.live.com/personal/19333ce569eb65e2/_layouts/15/doc.aspx?resid=939858de-3c2a-432f-b4ab-13acfc9deac8&cid=19333ce569eb65e2)[3](https://onedrive.live.com?cid=19333CE569EB65E2&id=19333CE569EB65E2!sf357b46f4a5b4a1cb8efec0fca50cac8)
        var mc = caseRepo.findById(dto.caseId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown caseId"));

        var p = patientRepo.findById(dto.patientId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown patientId"));

        var d = doctorRepo.findById(dto.doctorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown doctorId"));

        // 2) Build appointment (same fields your controller uses) [2](https://onedrive.live.com/personal/19333ce569eb65e2/_layouts/15/doc.aspx?resid=939858de-3c2a-432f-b4ab-13acfc9deac8&cid=19333ce569eb65e2)[1](https://onedrive.live.com/?id=5d41ae93-10d3-4c74-ab13-aab160e76ca3&cid=19333ce569eb65e2&web=1)
        var appt = Appointment.builder()
                .medicalCase(mc)
                .patient(p)
                .doctor(d)
                .apptTime(dto.apptTime())
                .status(AppointmentStatus.valueOf(dto.status() == null || dto.status().isBlank() ? "SCHEDULED" : dto.status()))
                .build();

        // 3) Persist
        appt = apptRepo.save(appt);

        // 4) Publish event (correct publisher type is ApplicationEventPublisher)
        eventPublisher.publishEvent(
                new AppointmentCreatedEvent(
                        appt.getId(),
                        appt.getDoctor().getId(),
                        appt.getPatient().getId(),
                        appt.getApptTime()
                )
        );

        return appt;
    }
}