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
import java.time.LocalDate;

@Component
public class InsurancePaymentStrategy implements PaymentStrategy {

    // Insurance covers 80% of the bill
    private static final BigDecimal COVERAGE_PERCENT =
        new BigDecimal("0.80");

    @Override
    public PaymentResult processPayment(PaymentRequest request) {
        if (request.getInsurance() == null) {
            throw new BusinessRuleException(
                "Patient has no insurance on file.");
        }

        // Check if insurance is still valid
        if (request.getInsurance().getValidTill().isBefore(LocalDate.now())) {
            throw new BusinessRuleException(
                "Patient's insurance policy has expired.");
        }

        BigDecimal total = request.getAmountToPay();

        BigDecimal insurancePays = total
                .multiply(COVERAGE_PERCENT)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal patientPays = total
                .subtract(insurancePays)
                .setScale(2, RoundingMode.HALF_UP);

        String ref = "INS-" + request.getInsurance().getPolicyNumber();

        return PaymentResult.builder()
                .success(true)
                .resultingStatus(BillStatus.PARTIALLY_PAID)
                .amountPaid(total)
                .insuranceCovered(insurancePays)
                .patientPaid(patientPays)
                .transactionReference(ref)
                .message("Insurance covers ₹" + insurancePays
                    + ". Patient pays ₹" + patientPays + ".")
                .build();
    }

    @Override
    public PaymentMethodType getPaymentMethod() {
        return PaymentMethodType.INSURANCE;
    }
}