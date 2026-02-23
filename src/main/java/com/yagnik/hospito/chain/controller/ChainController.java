package com.yagnik.hospito.chain.controller;

import com.yagnik.hospito.chain.dto.ChainResponse;
import com.yagnik.hospito.chain.dto.CreateChainRequest;
import com.yagnik.hospito.chain.service.ChainService;
import com.yagnik.hospito.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/chain")
@RequiredArgsConstructor
public class ChainController {

    private final ChainService chainService;

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<ChainResponse>> createChain(
            @Valid @RequestBody CreateChainRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                    chainService.createChain(request),
                    "Hospital chain created successfully"));
    }

    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<ChainResponse>> getChain() {
        return ResponseEntity.ok(
                ApiResponse.success(chainService.getChain()));
    }

    @PutMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<ChainResponse>> updateChain(
            @Valid @RequestBody CreateChainRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success(
                    chainService.updateChain(request),
                    "Hospital chain updated successfully"));
    }
}