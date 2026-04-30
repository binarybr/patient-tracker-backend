package com.binarysystempatienttracker.patienttracker.web.controller;

import com.binarysystempatienttracker.patienttracker.repository.AppointmentRepository;
import com.binarysystempatienttracker.patienttracker.repository.UserRepository;
import com.binarysystempatienttracker.patienttracker.service.ExcelExportService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/doctor/exports")
@AllArgsConstructor
public class DoctorExportController {
    private final AppointmentRepository appts;
    private final ExcelExportService excel;
    private final UserRepository users; // To resolve Doctor from Account

    @PreAuthorize("hasRole('DOCTOR')")
    @GetMapping(value = "/appointments.xlsx", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public void myAppointments(UserDetails ud, HttpServletResponse resp) throws IOException{
        var user = users.findByEmail(ud.getUsername()).orElseThrow();
        var doctor = user.getDoctor(); // If we store the link; otherwise look up email
        var list = appts.findByDoctor_Id(doctor.getId()); // Or range version if desired
        resp.setHeader("Content-Disposition", "attachment; filename=my_appointments.xlsx");
        excel.writeAppointments(list, resp.getOutputStream()); // Detailed writer already
    }
}
