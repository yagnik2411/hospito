package com.yagnik.hospito.billing.dto;

import com.yagnik.hospito.billing.enums.PaymentMethodType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProcessPaymentRequest {

    @NotNull(message = "Payment method is mandatory")
    private PaymentMethodType paymentMethod;

    // Required for CARD and UPI
    private String transactionReference;
}