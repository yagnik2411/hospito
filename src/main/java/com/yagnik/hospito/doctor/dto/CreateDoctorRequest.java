package com.yagnik.hospito.doctor.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateDoctorRequest {

    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotBlank(message = "Specialization is mandatory")
    private String specialization;

    @NotBlank(message = "License number is mandatory")
    private String licenseNumber;

    private String bio;

    @Email(message = "Must be a valid email")
    @NotBlank(message = "Email is mandatory")
    private String email;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotNull(message = "Branch ID is mandatory")
    private Long branchId;
}