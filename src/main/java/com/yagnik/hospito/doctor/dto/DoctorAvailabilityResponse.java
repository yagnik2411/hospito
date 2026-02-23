package com.yagnik.hospito.doctor.dto;

import com.yagnik.hospito.doctor.entity.DoctorAvailability.DayOfWeek;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DoctorAvailabilityResponse {
    private Long id;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private int slotDurationMinutes;
    private Long doctorId;
    private String doctorName;
}