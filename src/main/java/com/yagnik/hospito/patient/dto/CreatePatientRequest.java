package com.yagnik.hospito.patient.dto;

import com.yagnik.hospito.entity.types.BloodGroupType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreatePatientRequest {

    @NotBlank(message = "Name is mandatory")
    private String name;

    private String gender;

    @NotNull(message = "Date of birth is mandatory")
    private LocalDate dateOfBirth;

    private BloodGroupType bloodGroup;

    private String phone;

    private String address;

    @Email(message = "Must be a valid email")
    @NotBlank(message = "Email is mandatory")
    private String email;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    // Which branch they are registering at
    @NotNull(message = "Branch ID is mandatory")
    private Long branchId;
}