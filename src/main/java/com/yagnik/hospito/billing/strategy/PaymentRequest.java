package com.yagnik.hospito.billing.strategy;

import com.yagnik.hospito.billing.entity.Bill;
import com.yagnik.hospito.patient.entity.Insurance;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {
    private Bill bill;
    private BigDecimal amountToPay;
    private String transactionReference; // card/UPI ref
    private Insurance insurance;         // for insurance payment
}