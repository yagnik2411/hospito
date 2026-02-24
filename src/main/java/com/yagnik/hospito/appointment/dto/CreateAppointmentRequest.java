package com.yagnik.hospito.appointment.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateAppointmentRequest {

    @NotNull(message = "Patient ID is mandatory")
    private Long patientId;

    @NotNull(message = "Doctor ID is mandatory")
    private Long doctorId;

    @NotNull(message = "Branch ID is mandatory")
    private Long branchId;

    @NotNull(message = "Appointment time is mandatory")
    @Future(message = "Appointment time must be in the future")
    private LocalDateTime appointmentTime;

    @NotBlank(message = "Reason is mandatory")
    private String reason;
}