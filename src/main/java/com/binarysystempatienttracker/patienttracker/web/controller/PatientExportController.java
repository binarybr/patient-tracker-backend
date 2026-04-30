package com.binarysystempatienttracker.patienttracker.web.controller;

import com.binarysystempatienttracker.patienttracker.repository.AppointmentRepository;
import com.binarysystempatienttracker.patienttracker.repository.UserRepository;
import com.binarysystempatienttracker.patienttracker.service.ExcelExportService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/patient/exports")
@PreAuthorize("hasRole('PATIENT')")
@AllArgsConstructor
public class PatientExportController {
    private final AppointmentRepository appts;
    private final ExcelExportService excel;
    private final UserRepository users;

    public void myAppointments(UserDetails ud, HttpServletResponse resp) throws IOException{
        var user = users.findByEmail(ud.getUsername()).orElseThrow();
        var patient = user.getPatient();
        var list = appts.findByPatient_Id(patient.getId()); // or range version if desired
        resp.setHeader("Content-Disposition", "attachment; filename=my_appointments.xlsx");
        excel.writeAppointments(list, resp.getOutputStream()); // detailed writer
    }
}
