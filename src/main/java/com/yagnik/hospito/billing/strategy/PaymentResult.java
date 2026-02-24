package com.yagnik.hospito.billing.strategy;

import com.yagnik.hospito.billing.enums.BillStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResult {
    private boolean success;
    private BillStatus resultingStatus;
    private BigDecimal amountPaid;
    private BigDecimal insuranceCovered;
    private BigDecimal patientPaid;
    private String transactionReference;
    private String message;
}