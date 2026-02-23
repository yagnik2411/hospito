package com.yagnik.hospito.doctor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DoctorResponse {
    private Long id;
    private String name;
    private String specialization;
    private String licenseNumber;
    private String bio;
    private boolean isActive;
    private String email;
    private Long primaryBranchId;
    private String primaryBranchName;
    private Set<String> branchNames;
    private LocalDateTime createdAt;
}