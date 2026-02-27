package com.yagnik.hospito.branch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BranchResponse implements Serializable {
      @Serial
    private static final long serialVersionUID = 1L;
    private Long id;
    private String name;
    private String address;
    private String city;
    private String state;
    private String contactPhone;
    private String email;
    private boolean isActive;
    private Long chainId;
    private String chainName;
    private LocalDateTime createdAt;
}