package com.binarysystempatienttracker.patienttracker.service;

import com.binarysystempatienttracker.patienttracker.domain.Appointment;
import com.binarysystempatienttracker.patienttracker.domain.Doctor;
import com.binarysystempatienttracker.patienttracker.domain.MedicalCase;
import com.binarysystempatienttracker.patienttracker.domain.Patient;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Creates simple Excel workbooks for administrative exports
 * Uses Apache POI and writes directly to an {@link java.io.OutputStream} provided by the controller
 */
@Service
public class ExcelExportService {
    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Writes doctor details into a single-sheet workbook
     *
     * @param doctors
     * @param out
     * @throws IOException
     */
    public void writeDoctors(List<Doctor> doctors, OutputStream out) throws IOException {
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sh = wb.createSheet("Doctors");
            int r = 0;
            Row header = sh.createRow(r++);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("Name");
            header.createCell(2).setCellValue("Speciality");
            header.createCell(3).setCellValue("Hospital");
            header.createCell(4).setCellValue("Phone");
            header.createCell(5).setCellValue("Approved");
            for (Doctor d : doctors) {
                Row row = sh.createRow(r++);
                header.createCell(0).setCellValue(d.getId());
                header.createCell(1).setCellValue(nvl(d.getName()));
                header.createCell(2).setCellValue(nvl(d.getSpeciality()));
                header.createCell(3).setCellValue(nvl(d.getHospital()));
                header.createCell(4).setCellValue(nvl(d.getPhone()));
                header.createCell(5).setCellValue(d.isApproved());
            }
            autoSize(sh, 6);
            wb.write(out);
        }
    }

    /**
     * Writes basic patient details into a workbook
     *
     * @param patients
     * @param out
     * @throws IOException
     */
    public void writePatients(List<Patient> patients, OutputStream out) throws IOException {
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sh = wb.createSheet("Patients");
            int r = 0;
            Row header = sh.createRow(r++);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("Name");
            header.createCell(2).setCellValue("DOB");
            header.createCell(3).setCellValue("Phone");
            header.createCell(4).setCellValue("Gender");
            for (Patient p : patients) {
                Row row = sh.createRow(r++);
                row.createCell(0).setCellValue(p.getId());
                row.createCell(1).setCellValue(nvl(p.getName()));
                row.createCell(2).setCellValue(p.getDob() == null ? "" : p.getDob().toString());
                row.createCell(3).setCellValue(nvl(p.getPhone()));
                row.createCell(4).setCellValue(nvl(p.getGender()));

            }
            autoSize(sh, 5);
            wb.write(out);
        }
    }

    /**
     * Writes appointments with IDs and timestamps
     *
     * @param appts
     * @param out
     * @throws IOException
     */
    public void writeAppointments(List<Appointment> appts, OutputStream out) throws IOException {
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sh = wb.createSheet("Appointments");
            int r = 0;

            // Header
            Row header = sh.createRow(r++);
            int c = 0;

            header.createCell(c++).setCellValue("ApptID");
            header.createCell(c++).setCellValue("Time (UTC)");
            header.createCell(c++).setCellValue("Status");

            header.createCell(c++).setCellValue("CaseID");
            header.createCell(c++).setCellValue("Case Title");
            header.createCell(c++).setCellValue("Case Diagnosis");
            header.createCell(c++).setCellValue("Case Symptoms");

            header.createCell(c++).setCellValue("PatientID");
            header.createCell(c++).setCellValue("Patient Name");
            header.createCell(c++).setCellValue("Patient Gender");
            header.createCell(c++).setCellValue("Patient DOB");

            header.createCell(c++).setCellValue("DoctorID");
            header.createCell(c++).setCellValue("Doctor Name");
            header.createCell(c++).setCellValue("Doctor Speciality");
            header.createCell(c++).setCellValue("Doctor Hospital");
            header.createCell(c++).setCellValue("Doctor Phone");

            for (Appointment a : appts) {
                Row row = sh.createRow(r++);
                int i = 0;

                // Basic Appointment
                row.createCell(i++).setCellValue(a.getId());
                row.createCell(i++).setCellValue(a.getApptTime() == null ? "" : TS.format(a.getApptTime()));
//                row.createCell(i++).setCellValue(nvl(a.getStatus()));
                row.createCell(i++).setCellValue(a.getStatus().name());

                // Case Details
                MedicalCase mc = a.getMedicalCase();
                row.createCell(i++).setCellValue(mc != null ? mc.getId() : nullSafeNum(null));
                row.createCell(i++).setCellValue(mc != null ? nvl(mc.getTitle()) : "");
                row.createCell(i++).setCellValue(mc != null ? nvl(mc.getDiagnosis()) : "");
                row.createCell(i++).setCellValue(mc != null ? nvl(mc.getSymptoms()) : "");

                // Patient Details
                Patient p = a.getPatient();
                row.createCell(i++).setCellValue(p != null ? p.getId() : nullSafeNum(null));
                row.createCell(i++).setCellValue(p != null ? nvl(p.getName()) : "");
                row.createCell(i++).setCellValue(p != null ? nvl(p.getGender()) : "");
                row.createCell(i++).setCellValue(p != null ? (p.getDob()) == null ? "" : p.getDob().toString() : "");

                // Doctor Details
                Doctor d = a.getDoctor();
                row.createCell(i++).setCellValue(d != null ? d.getId() : nullSafeNum(null));
                row.createCell(i++).setCellValue(d != null ? nvl(d.getName()) : "");
                row.createCell(i++).setCellValue(d != null ? nvl(d.getSpeciality()) : "");
                row.createCell(i++).setCellValue(d != null ? nvl(d.getHospital()) : "");
                row.createCell(i++).setCellValue(d != null ? nvl(d.getPhone()) : "");

            }
            autoSize(sh, 16);
            wb.write(out);
        }
    }

    private void autoSize(Sheet sh, int cols) {
        for (int i = 0; i < cols; i++) {
            sh.autoSizeColumn(i);
        }
    }

    private String nvl(String s) {
        return s == null ? "" : s;
    }

    // When setting numeric cell with possible null id
    private double nullSafeNum(Long val) {
        return val == null ? 0D : val.doubleValue();
    }

}
