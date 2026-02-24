package com.yagnik.hospito.billing.strategy.impl;

import com.yagnik.hospito.billing.enums.BillStatus;
import com.yagnik.hospito.billing.enums.PaymentMethodType;
import com.yagnik.hospito.billing.strategy.PaymentRequest;
import com.yagnik.hospito.billing.strategy.PaymentResult;
import com.yagnik.hospito.billing.strategy.PaymentStrategy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class CashPaymentStrategy implements PaymentStrategy {

    @Override
    public PaymentResult processPayment(PaymentRequest request) {
        // Cash payment — straightforward, no gateway needed
        String ref = "CASH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        return PaymentResult.builder()
                .success(true)
                .resultingStatus(BillStatus.PAID)
                .amountPaid(request.getAmountToPay())
                .insuranceCovered(BigDecimal.ZERO)
                .patientPaid(request.getAmountToPay())
                .transactionReference(ref)
                .message("Cash payment recorded successfully.")
                .build();
    }

    @Override
    public PaymentMethodType getPaymentMethod() {
        return PaymentMethodType.CASH;
    }
}