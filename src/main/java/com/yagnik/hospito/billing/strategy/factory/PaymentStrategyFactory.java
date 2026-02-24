package com.yagnik.hospito.billing.strategy.factory;

import com.yagnik.hospito.billing.enums.PaymentMethodType;
import com.yagnik.hospito.billing.strategy.PaymentStrategy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class PaymentStrategyFactory {

    private final Map<PaymentMethodType, PaymentStrategy> strategyMap;

    // Spring injects all PaymentStrategy beans automatically
    public PaymentStrategyFactory(List<PaymentStrategy> strategies) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(
                    PaymentStrategy::getPaymentMethod,
                    Function.identity()));
    }

    public PaymentStrategy getStrategy(PaymentMethodType method) {
        PaymentStrategy strategy = strategyMap.get(method);
        if (strategy == null) {
            throw new IllegalArgumentException(
                "No payment strategy found for method: " + method);
        }
        return strategy;
    }
}