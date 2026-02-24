package com.yagnik.hospito.patient.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MedicalRecordRequest {

    @NotBlank(message = "Diagnosis is mandatory")
    private String diagnosis;

    private String prescription;

    private String notes;

    @NotNull(message = "Record date is mandatory")
    private LocalDate recordDate;

    @NotNull(message = "Doctor ID is mandatory")
    private Long doctorId;
}