package com.yagnik.hospito.doctor.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TransferDoctorRequest {

    @NotNull(message = "Target branch ID is mandatory")
    private Long targetBranchId;
}