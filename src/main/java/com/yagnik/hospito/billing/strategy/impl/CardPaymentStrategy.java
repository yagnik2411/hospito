package com.yagnik.hospito.billing.strategy.impl;

import com.yagnik.hospito.billing.enums.BillStatus;
import com.yagnik.hospito.billing.enums.PaymentMethodType;
import com.yagnik.hospito.billing.strategy.PaymentRequest;
import com.yagnik.hospito.billing.strategy.PaymentResult;
import com.yagnik.hospito.billing.strategy.PaymentStrategy;
import com.yagnik.hospito.common.exception.BusinessRuleException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class CardPaymentStrategy implements PaymentStrategy {

    // 1.5% processing fee for card payments
    private static final BigDecimal CARD_FEE_PERCENT =
        new BigDecimal("0.015");

    @Override
    public PaymentResult processPayment(PaymentRequest request) {
        if (request.getTransactionReference() == null ||
            request.getTransactionReference().isBlank()) {
            throw new BusinessRuleException(
                "Card transaction reference is required for card payment.");
        }

        // Apply card processing fee on top of amount
        BigDecimal fee = request.getAmountToPay()
                .multiply(CARD_FEE_PERCENT)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalCharged = request.getAmountToPay().add(fee);

        return PaymentResult.builder()
                .success(true)
                .resultingStatus(BillStatus.PAID)
                .amountPaid(totalCharged)
                .insuranceCovered(BigDecimal.ZERO)
                .patientPaid(totalCharged)
                .transactionReference(request.getTransactionReference())
                .message("Card payment processed. Processing fee of ₹"
                    + fee + " applied.")
                .build();
    }

    @Override
    public PaymentMethodType getPaymentMethod() {
        return PaymentMethodType.CARD;
    }
}