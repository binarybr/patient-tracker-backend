package com.binarysystempatienttracker.patienttracker.service.caseversion;

import com.binarysystempatienttracker.patienttracker.domain.MedicalCase;
import com.binarysystempatienttracker.patienttracker.domain.caseversion.MedicalCaseVersion;
import com.binarysystempatienttracker.patienttracker.repository.MedicalCaseVersionRepository;
import com.binarysystempatienttracker.patienttracker.web.dto.CaseDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MedicalCaseVersionService {

    private final MedicalCaseVersionRepository repo;

    @Transactional
    public MedicalCaseVersion createVersion(MedicalCase c, CaseDto dto) {
        int nextVersion = repo.maxVersion(c.getId()).orElse(0) + 1;

        return repo.save(
                MedicalCaseVersion.builder()
                        .medicalCase(c)
                        .version(nextVersion)
                        .diagnosis(dto.diagnosis())
                        .symptoms(dto.symptoms())
                        .medicines(dto.medicines())
                        .createdAt(LocalDateTime.now())
                        .build()
        );
    }
}
