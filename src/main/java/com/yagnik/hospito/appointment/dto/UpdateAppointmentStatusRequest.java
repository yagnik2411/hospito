package com.yagnik.hospito.appointment.dto;

import com.yagnik.hospito.appointment.enums.AppointmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateAppointmentStatusRequest {

    @NotNull(message = "Status is mandatory")
    private AppointmentStatus status;

    private String notes;
}