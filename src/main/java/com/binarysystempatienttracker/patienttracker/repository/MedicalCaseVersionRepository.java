package com.binarysystempatienttracker.patienttracker.repository;

import com.binarysystempatienttracker.patienttracker.domain.caseversion.MedicalCaseVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MedicalCaseVersionRepository
        extends JpaRepository<MedicalCaseVersion, Long> {

    List<MedicalCaseVersion> findByMedicalCase_IdOrderByVersionDesc(Long medicalCaseId);

    @Query("select max(v.version) from MedicalCaseVersion v where v.medicalCase.id = :caseId")
    Optional<Integer> maxVersion(Long caseId);
}