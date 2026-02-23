package com.yagnik.hospito.doctor.dto;

import com.yagnik.hospito.doctor.entity.DoctorAvailability.DayOfWeek;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalTime;

@Data
public class DoctorAvailabilityRequest {

    @NotNull(message = "Day of week is mandatory")
    private DayOfWeek dayOfWeek;

    @NotNull(message = "Start time is mandatory")
    private LocalTime startTime;

    @NotNull(message = "End time is mandatory")
    private LocalTime endTime;

    @NotNull(message = "Slot duration is mandatory")
    private Integer slotDurationMinutes;
}