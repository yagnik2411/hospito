package com.yagnik.hospito.billing.producer;

import com.yagnik.hospito.common.event.HospitoEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class BillingEventProducer {

    private final KafkaTemplate<String, HospitoEvent> kafkaTemplate;

    private static final String TOPIC = "billing-events";

    public void publishBillPaid(HospitoEvent event) {
        event.setEventType(HospitoEvent.EventType.BILL_PAID);
        event.setOccurredAt(LocalDateTime.now());
        kafkaTemplate.send(TOPIC, String.valueOf(event.getBillId()), event);
        log.info("📤 Published BILL_PAID event for billId={}", event.getBillId());
    }
}