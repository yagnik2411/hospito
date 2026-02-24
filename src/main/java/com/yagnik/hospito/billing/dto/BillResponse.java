package com.yagnik.hospito.billing.dto;

import com.yagnik.hospito.billing.enums.BillStatus;
import com.yagnik.hospito.billing.enums.PaymentMethodType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BillResponse {
    private Long id;
    private Long appointmentId;
    private Long patientId;
    private String patientName;
    private Long branchId;
    private String branchName;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal taxAmount;
    private BigDecimal finalAmount;
    private PaymentMethodType paymentMethod;
    private BillStatus status;
    private BigDecimal insuranceCoveredAmount;
    private BigDecimal patientPayableAmount;
    private String transactionReference;
    private String notes;
    private String paymentMessage;
    private List<BillItemResponse> items;
    private LocalDateTime createdAt;
}