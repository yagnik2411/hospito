package com.yagnik.hospito.chain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChainResponse {
    private Long id;
    private String name;
    private String registrationNumber;
    private Integer foundedYear;
    private String headOfficeAddress;
    private String email;
    private String contactPhone;
    private String description;
    private boolean isActive;
    private LocalDateTime createdAt;
}