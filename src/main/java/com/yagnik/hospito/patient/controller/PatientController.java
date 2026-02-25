package com.yagnik.hospito.patient.controller;

import com.yagnik.hospito.common.response.ApiResponse;
import com.yagnik.hospito.patient.dto.*;
import com.yagnik.hospito.patient.service.PatientService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
@Tag(name = "Patient", description = "Patient registration, medical records, insurance")
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    public ResponseEntity<ApiResponse<PatientResponse>> createPatient(
            @Valid @RequestBody CreatePatientRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                    patientService.createPatient(request),
                    "Patient registered successfully"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'BRANCH_ADMIN')")
    public ResponseEntity<ApiResponse<List<PatientResponse>>> getPatientsByBranch(
            @RequestParam Long branchId,
            @RequestParam(required = false) String name) {
        return ResponseEntity.ok(
                ApiResponse.success(
                    patientService.getPatientsByBranch(branchId, name)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'BRANCH_ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<ApiResponse<PatientResponse>> getPatientById(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success(patientService.getPatientById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'BRANCH_ADMIN', 'PATIENT')")
    public ResponseEntity<ApiResponse<PatientResponse>> updatePatient(
            @PathVariable Long id,
            @Valid @RequestBody CreatePatientRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success(
                    patientService.updatePatient(id, request),
                    "Patient updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deletePatient(
            @PathVariable Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Patient deactivated successfully"));
    }

    @PostMapping("/{id}/records")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'DOCTOR')")
    public ResponseEntity<ApiResponse<MedicalRecordResponse>> addMedicalRecord(
            @PathVariable Long id,
            @Valid @RequestBody MedicalRecordRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                    patientService.addMedicalRecord(id, request),
                    "Medical record added successfully"));
    }

    @GetMapping("/{id}/records")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'BRANCH_ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<ApiResponse<List<MedicalRecordResponse>>> getMedicalRecords(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success(patientService.getMedicalRecords(id)));
    }

    @PutMapping("/{id}/insurance")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'BRANCH_ADMIN', 'PATIENT')")
    public ResponseEntity<ApiResponse<InsuranceResponse>> assignInsurance(
            @PathVariable Long id,
            @Valid @RequestBody InsuranceRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success(
                    patientService.assignInsurance(id, request),
                    "Insurance assigned successfully"));
    }

    @GetMapping("/{id}/insurance")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'BRANCH_ADMIN', 'PATIENT')")
    public ResponseEntity<ApiResponse<InsuranceResponse>> getInsurance(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success(patientService.getInsurance(id)));
    }
}