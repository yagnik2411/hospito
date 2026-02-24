package com.yagnik.hospito.appointment.controller;

import com.yagnik.hospito.appointment.dto.*;
import com.yagnik.hospito.appointment.enums.AppointmentStatus;
import com.yagnik.hospito.appointment.service.AppointmentService;
import com.yagnik.hospito.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'BRANCH_ADMIN', 'PATIENT')")
    public ResponseEntity<ApiResponse<AppointmentResponse>> createAppointment(
            @Valid @RequestBody CreateAppointmentRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                    appointmentService.createAppointment(request),
                    "Appointment booked successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'BRANCH_ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<ApiResponse<AppointmentResponse>> getAppointmentById(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success(appointmentService.getAppointmentById(id)));
    }

    @GetMapping("/branch/{branchId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'BRANCH_ADMIN')")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getByBranch(
            @PathVariable Long branchId,
            @RequestParam(required = false) AppointmentStatus status) {
        return ResponseEntity.ok(
                ApiResponse.success(
                    appointmentService.getAppointmentsByBranch(branchId, status)));
    }

    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'BRANCH_ADMIN', 'DOCTOR')")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getByDoctor(
            @PathVariable Long doctorId,
            @RequestParam(required = false) AppointmentStatus status) {
        return ResponseEntity.ok(
                ApiResponse.success(
                    appointmentService.getAppointmentsByDoctor(doctorId, status)));
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'BRANCH_ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getByPatient(
            @PathVariable Long patientId) {
        return ResponseEntity.ok(
                ApiResponse.success(
                    appointmentService.getAppointmentsByPatient(patientId)));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'BRANCH_ADMIN', 'DOCTOR')")
    public ResponseEntity<ApiResponse<AppointmentResponse>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAppointmentStatusRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success(
                    appointmentService.updateStatus(id, request),
                    "Appointment status updated"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'BRANCH_ADMIN', 'PATIENT')")
    public ResponseEntity<ApiResponse<Void>> cancelAppointment(
            @PathVariable Long id) {
        appointmentService.cancelAppointment(id);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Appointment cancelled successfully"));
    }
}