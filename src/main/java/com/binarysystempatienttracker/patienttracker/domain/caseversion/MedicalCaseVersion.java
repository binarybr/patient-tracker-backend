package com.binarysystempatienttracker.patienttracker.domain.caseversion;

import com.binarysystempatienttracker.patienttracker.domain.MedicalCase;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalCaseVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private MedicalCase medicalCase;

    private int version;

    @Lob private String diagnosis;
    @Lob
    private String symptoms;
    @Lob private String medicines;

    private LocalDateTime createdAt;
}
