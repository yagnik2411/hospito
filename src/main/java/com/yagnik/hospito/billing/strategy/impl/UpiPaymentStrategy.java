package com.yagnik.hospito.billing.strategy.impl;

import com.yagnik.hospito.billing.enums.BillStatus;
import com.yagnik.hospito.billing.enums.PaymentMethodType;
import com.yagnik.hospito.billing.strategy.PaymentRequest;
import com.yagnik.hospito.billing.strategy.PaymentResult;
import com.yagnik.hospito.billing.strategy.PaymentStrategy;
import com.yagnik.hospito.common.exception.BusinessRuleException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class UpiPaymentStrategy implements PaymentStrategy {

    @Override
    public PaymentResult processPayment(PaymentRequest request) {
        if (request.getTransactionReference() == null ||
            request.getTransactionReference().isBlank()) {
            throw new BusinessRuleException(
                "UPI transaction ID is required for UPI payment.");
        }

        // Validate UPI ID format — must contain @
        String ref = request.getTransactionReference();
        if (!ref.contains("@")) {
            throw new BusinessRuleException(
                "Invalid UPI transaction ID format. Must contain '@'.");
        }

        return PaymentResult.builder()
                .success(true)
                .resultingStatus(BillStatus.PAID)
                .amountPaid(request.getAmountToPay())
                .insuranceCovered(BigDecimal.ZERO)
                .patientPaid(request.getAmountToPay())
                .transactionReference(ref)
                .message("UPI payment processed successfully. Instant settlement.")
                .build();
    }

    @Override
    public PaymentMethodType getPaymentMethod() {
        return PaymentMethodType.UPI;
    }
}