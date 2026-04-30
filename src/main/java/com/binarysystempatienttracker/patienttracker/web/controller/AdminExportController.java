package com.binarysystempatienttracker.patienttracker.web.controller;

import com.binarysystempatienttracker.patienttracker.repository.AppointmentRepository;
import com.binarysystempatienttracker.patienttracker.repository.DoctorRepository;
import com.binarysystempatienttracker.patienttracker.repository.PatientRepository;
import com.binarysystempatienttracker.patienttracker.service.ExcelExportService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/admin/exports")
@AllArgsConstructor
public class AdminExportController {
    private final DoctorRepository doctors;
    private final PatientRepository patients;
    private final AppointmentRepository appts;
    private final ExcelExportService excel;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/doctors.xlsx", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public void doctorsXlsx(HttpServletResponse resp) throws IOException{
        resp.setHeader("Content-Disposition", "attachment; filename=doctors.xlsx");
        excel.writeDoctors(doctors.findAll(), resp.getOutputStream());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/patients.xlsx", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public void patientsXlsx(HttpServletResponse resp) throws IOException{
        resp.setHeader("Content-Disposition", "attachment; filename=patients.xlsx");
        excel.writePatients(patients.findAll(), resp.getOutputStream());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/doctors-appointments.xlsx", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public void doctorApptsXlsx(@RequestParam Long doctorId,
                                @RequestParam(required = false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
                                @RequestParam(required = false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
                                @RequestParam(required = false, defaultValue = "") String window,
                                HttpServletResponse resp) throws IOException{
        // Resolve timen window
        LocalDateTime[] range = resolveWindow(from, to, window);
        LocalDateTime f= range[0];
        LocalDateTime t= range[1];
        var list = appts.findByDoctor_IdAndApptTimeBetween(doctorId, f, t);
        resp.setHeader("Content-Disposition", "attachment; filename=doctors_" + doctorId + "_appointments.xlsx");
        excel.writeAppointments(list, resp.getOutputStream());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/appointments.xlsx", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public void allApptsXlsx(HttpServletResponse resp) throws IOException{
        resp.setHeader("Content-Disposition", "attachment; filename=appointments.xlsx");
        var list=appts.findAll();
        excel.writeAppointments(list, resp.getOutputStream());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/patient-appointments.xlsx", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public void patientApptsXlsx(@RequestParam Long patientId,
                                @RequestParam(required = false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
                                @RequestParam(required = false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
                                @RequestParam(required = false, defaultValue = "") String window,
                                HttpServletResponse resp) throws IOException{
        // Resolve timen window
        LocalDateTime[] range = resolveWindow(from, to, window);
        LocalDateTime f= range[0];
        LocalDateTime t= range[1];
        var list = appts.findByPatient_IdAndApptTimeBetween(patientId, f, t);
        resp.setHeader("Content-Disposition", "attachment; filename=patient_" + patientId + "_appointments.xlsx");
        excel.writeAppointments(list, resp.getOutputStream());
    }

    /**
     * Resolves the time window for exports
     * If both 'from' and 'to' are null:
     * - window=history -> [now-10y, now]
     * - window=upcoming -> [now, now+2y]
     * - default -> [now-10y, now+2y]
     * If either 'from' or 'to' is provided, it uses them AS-IS (missing bound is substituted)
     * @param from
     * @param to
     * @param window
     * @return
     */
    private LocalDateTime[] resolveWindow(LocalDateTime from, LocalDateTime to, String window){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime f = from;
        LocalDateTime t = to;

        if (f==null && t==null){
            if ("history".equalsIgnoreCase(window)){
                t=now;
                f=now.minusYears(10);
            } else if ("upcoming".equalsIgnoreCase(window)) {
                f=now;
                t=now.plusYears(2);
            }else {
                f=now.minusYears(10);
                t=now.plusYears(2);
            }
        }

        // If only one bound is supplied by caller, substitute a sensible default on the other side
        return new LocalDateTime[]{
                f!=null?f:now.minusYears(10),
                t!=null?t:now.plusYears(2)
        };
    }
}
