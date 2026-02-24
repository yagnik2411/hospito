package com.yagnik.hospito.appointment.dto;

import com.yagnik.hospito.appointment.enums.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentResponse {
    private Long id;
    private LocalDateTime appointmentTime;
    private String reason;
    private AppointmentStatus status;
    private String notes;
    private Long patientId;
    private String patientName;
    private Long doctorId;
    private String doctorName;
    private String doctorSpecialization;
    private Long branchId;
    private String branchName;
    private LocalDateTime createdAt;
}