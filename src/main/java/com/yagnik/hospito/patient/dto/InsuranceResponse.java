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
public class InsuranceResponse {
    private Long id;
    private String policyNumber;
    private String provider;
    private LocalDate validTill;
    private Long patientId;
    private String patientName;
}