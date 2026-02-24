package com.yagnik.hospito.appointment.service;

import com.yagnik.hospito.appointment.dto.*;
import com.yagnik.hospito.appointment.enums.AppointmentStatus;

import java.util.List;

public interface AppointmentService {
    AppointmentResponse createAppointment(CreateAppointmentRequest request);
    AppointmentResponse getAppointmentById(Long id);
    List<AppointmentResponse> getAppointmentsByBranch(Long branchId, AppointmentStatus status);
    List<AppointmentResponse> getAppointmentsByDoctor(Long doctorId, AppointmentStatus status);
    List<AppointmentResponse> getAppointmentsByPatient(Long patientId);
    AppointmentResponse updateStatus(Long id, UpdateAppointmentStatusRequest request);
    void cancelAppointment(Long id);
}