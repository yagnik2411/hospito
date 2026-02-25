package com.yagnik.hospito.patient.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import com.yagnik.hospito.patient.enums.BloodGroupType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PatientResponse {
    private Long id;
    private String name;
    private String gender;
    private LocalDate dateOfBirth;
    private BloodGroupType bloodGroup;
    private String phone;
    private String address;
    private boolean isActive;
    private String email;
    private Set<String> visitedBranchNames;
    private LocalDateTime createdAt;
}