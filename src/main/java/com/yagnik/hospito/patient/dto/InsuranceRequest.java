package com.yagnik.hospito.patient.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class InsuranceRequest {

    @NotBlank(message = "Policy number is mandatory")
    private String policyNumber;

    @NotBlank(message = "Provider is mandatory")
    private String provider;

    @NotNull(message = "Valid till date is mandatory")
    private LocalDate validTill;
}