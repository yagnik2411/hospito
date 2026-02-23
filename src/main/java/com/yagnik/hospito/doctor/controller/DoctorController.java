package com.yagnik.hospito.doctor.controller;

import com.yagnik.hospito.common.response.ApiResponse;
import com.yagnik.hospito.doctor.dto.*;
import com.yagnik.hospito.doctor.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'BRANCH_ADMIN')")
    public ResponseEntity<ApiResponse<DoctorResponse>> createDoctor(
            @Valid @RequestBody CreateDoctorRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                    doctorService.createDoctor(request),
                    "Doctor created successfully"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'BRANCH_ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<ApiResponse<List<DoctorResponse>>> getDoctorsByBranch(
            @RequestParam Long branchId,
            @RequestParam(required = false) String specialization) {
        return ResponseEntity.ok(
                ApiResponse.success(
                    doctorService.getDoctorsByBranch(branchId, specialization)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'BRANCH_ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<ApiResponse<DoctorResponse>> getDoctorById(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success(doctorService.getDoctorById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'BRANCH_ADMIN')")
    public ResponseEntity<ApiResponse<DoctorResponse>> updateDoctor(
            @PathVariable Long id,
            @Valid @RequestBody CreateDoctorRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success(
                    doctorService.updateDoctor(id, request),
                    "Doctor updated successfully"));
    }

    @PostMapping("/{id}/transfer")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<DoctorResponse>> transferDoctor(
            @PathVariable Long id,
            @Valid @RequestBody TransferDoctorRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success(
                    doctorService.transferDoctor(id, request),
                    "Doctor transferred successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteDoctor(
            @PathVariable Long id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Doctor deactivated successfully"));
    }

    @PutMapping("/{id}/availability")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'DOCTOR')")
    public ResponseEntity<ApiResponse<List<DoctorAvailabilityResponse>>> setAvailability(
            @PathVariable Long id,
            @Valid @RequestBody List<DoctorAvailabilityRequest> request) {
        return ResponseEntity.ok(
                ApiResponse.success(
                    doctorService.setAvailability(id, request),
                    "Availability updated successfully"));
    }

    @GetMapping("/{id}/availability")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'BRANCH_ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<ApiResponse<List<DoctorAvailabilityResponse>>> getAvailability(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success(doctorService.getAvailability(id)));
    }
}