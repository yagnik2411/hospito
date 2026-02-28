package com.yagnik.notification.consumer;

import com.yagnik.notification.event.HospitoEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BillingNotificationConsumer {

    @KafkaListener(topics = "billing-events", groupId = "notification-group")
    public void handleBillingEvent(HospitoEvent event) {
        switch (event.getEventType()) {

            case BILL_PAID -> {
                log.info("═══════════════════════════════════════════════════");
                log.info("💰 PAYMENT RECEIPT NOTIFICATION");
                log.info("   To:        {}", event.getPatientEmail());
                log.info("   Patient:   {}", event.getPatientName());
                log.info("   Bill ID:   {}", event.getBillId());
                log.info("   Amount:    ₹{}", event.getAmountPaid());
                log.info("   Method:    {}", event.getPaymentMethod());
                log.info("   Ref:       {}", event.getTransactionReference());
                log.info("   MSG:       Payment received successfully. Thank you!");
                log.info("═══════════════════════════════════════════════════");
            }

            default -> log.warn("⚠️ Unknown billing event type: {}", event.getEventType());
        }
    }
}