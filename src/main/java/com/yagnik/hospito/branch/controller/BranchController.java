package com.yagnik.hospito.branch.controller;

import com.yagnik.hospito.branch.dto.BranchResponse;
import com.yagnik.hospito.branch.dto.CreateBranchRequest;
import com.yagnik.hospito.branch.service.BranchService;
import com.yagnik.hospito.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/branches")
@RequiredArgsConstructor
public class BranchController {

    private final BranchService branchService;

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<BranchResponse>> createBranch(
            @Valid @RequestBody CreateBranchRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                    branchService.createBranch(request),
                    "Branch created successfully"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'BRANCH_ADMIN')")
    public ResponseEntity<ApiResponse<List<BranchResponse>>> getAllBranches() {
        return ResponseEntity.ok(
                ApiResponse.success(branchService.getAllBranches()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'BRANCH_ADMIN')")
    public ResponseEntity<ApiResponse<BranchResponse>> getBranchById(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success(branchService.getBranchById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<BranchResponse>> updateBranch(
            @PathVariable Long id,
            @Valid @RequestBody CreateBranchRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success(
                    branchService.updateBranch(id, request),
                    "Branch updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteBranch(
            @PathVariable Long id) {
        branchService.deleteBranch(id);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Branch deactivated successfully"));
    }
}