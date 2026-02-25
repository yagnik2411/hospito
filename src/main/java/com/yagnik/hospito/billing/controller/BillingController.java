package com.yagnik.hospito.billing.controller;

import com.yagnik.hospito.billing.dto.*;
import com.yagnik.hospito.billing.enums.BillStatus;
import com.yagnik.hospito.billing.service.BillingService;
import com.yagnik.hospito.common.response.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bills")
@RequiredArgsConstructor
@Tag(name = "Billing", description = "Bill creation and payment processing")
public class BillingController {

    private final BillingService billingService;

    @Operation(summary = "Create a bill for a completed appointment")
    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'BRANCH_ADMIN')")
    public ResponseEntity<ApiResponse<BillResponse>> createBill(
            @Valid @RequestBody CreateBillRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        billingService.createBill(request),
                        "Bill created successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'BRANCH_ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<ApiResponse<BillResponse>> getBillById(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success(billingService.getBillById(id)));
    }

    @GetMapping("/appointment/{appointmentId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'BRANCH_ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<ApiResponse<BillResponse>> getBillByAppointment(
            @PathVariable Long appointmentId) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        billingService.getBillByAppointment(appointmentId)));
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'BRANCH_ADMIN', 'PATIENT')")
    public ResponseEntity<ApiResponse<List<BillResponse>>> getBillsByPatient(
            @PathVariable Long patientId) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        billingService.getBillsByPatient(patientId)));
    }

    @GetMapping("/branch/{branchId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'BRANCH_ADMIN')")
    public ResponseEntity<ApiResponse<List<BillResponse>>> getBillsByBranch(
            @PathVariable Long branchId,
            @RequestParam(required = false) BillStatus status) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        billingService.getBillsByBranch(branchId, status)));
    }

    @Operation(summary = "Process payment", description = "Supports CASH, CARD (1.5% fee), UPI (@ required), INSURANCE (80% coverage)")
    @PostMapping("/{id}/pay")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'BRANCH_ADMIN')")
    public ResponseEntity<ApiResponse<BillResponse>> processPayment(
            @PathVariable Long id,
            @Valid @RequestBody ProcessPaymentRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        billingService.processPayment(id, request),
                        "Payment processed successfully"));
    }

    @Operation(summary = "Waive a bill (SUPER_ADMIN only)")
    @PatchMapping("/{id}/waive")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<BillResponse>> waiveBill(
            @PathVariable Long id,
            @RequestParam(required = false) String notes) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        billingService.waiveBill(id, notes),
                        "Bill waived successfully"));
    }
}