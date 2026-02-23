package com.yagnik.hospito.chain.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateChainRequest {

    @NotBlank(message = "Name is mandatory")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    private String name;

    @NotBlank(message = "Registration number is mandatory")
    private String registrationNumber;

    @NotNull(message = "Founded year is mandatory")
    @Min(value = 1800, message = "Founded year seems too old")
    @Max(value = 2100, message = "Founded year seems too far in future")
    private Integer foundedYear;

    @NotBlank(message = "Head office address is mandatory")
    private String headOfficeAddress;

    @Email(message = "Must be a valid email")
    @NotBlank(message = "Email is mandatory")
    private String email;

    private String contactPhone;
    private String description;
}