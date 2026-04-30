package com.binarysystempatienttracker.patienttracker.repository;

import com.binarysystempatienttracker.patienttracker.domain.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Appointment queries for common lookups and time-window filtering
 */
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByDoctor_Id(Long doctorId);

    List<Appointment> findByPatient_Id(Long patientId);

    List<Appointment> findByApptTimeBetween(LocalDateTime from, LocalDateTime to);

    //    Fetch appointments for a doctor constrained to a time window
    List<Appointment> findByDoctor_IdAndApptTimeBetween(Long doctorId, LocalDateTime from, LocalDateTime to);

    //    Fetch appointments for a patient constrained to a time window
    List<Appointment> findByPatient_IdAndApptTimeBetween(Long patientId, LocalDateTime from, LocalDateTime to);

}
