package com.yagnik.hospito.billing.strategy;

import com.yagnik.hospito.billing.enums.PaymentMethodType;

public interface PaymentStrategy {
    PaymentResult processPayment(PaymentRequest request);
    PaymentMethodType getPaymentMethod();
}