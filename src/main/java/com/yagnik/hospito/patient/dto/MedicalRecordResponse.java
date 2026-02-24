package com.yagnik.hospito.patient.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MedicalRecordResponse {
    private Long id;
    private String diagnosis;
    private String prescription;
    private String notes;
    private LocalDate recordDate;
    private Long patientId;
    private String patientName;
    private Long doctorId;
    private String doctorName;
}