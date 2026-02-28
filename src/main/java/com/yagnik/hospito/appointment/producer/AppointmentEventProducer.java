package com.yagnik.hospito.appointment.producer;

import com.yagnik.hospito.common.event.HospitoEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppointmentEventProducer {

    private final KafkaTemplate<String, HospitoEvent> kafkaTemplate;

    private static final String TOPIC = "appointment-events";

    public void publishAppointmentBooked(HospitoEvent event) {
        event.setEventType(HospitoEvent.EventType.APPOINTMENT_BOOKED);
        event.setOccurredAt(LocalDateTime.now());
        kafkaTemplate.send(TOPIC, String.valueOf(event.getAppointmentId()), event);
        log.info("📤 Published APPOINTMENT_BOOKED event for appointmentId={}", event.getAppointmentId());
    }

    public void publishAppointmentConfirmed(HospitoEvent event) {
        event.setEventType(HospitoEvent.EventType.APPOINTMENT_CONFIRMED);
        event.setOccurredAt(LocalDateTime.now());
        kafkaTemplate.send(TOPIC, String.valueOf(event.getAppointmentId()), event);
        log.info("📤 Published APPOINTMENT_CONFIRMED event for appointmentId={}", event.getAppointmentId());
    }

    public void publishAppointmentCompleted(HospitoEvent event) {
        event.setEventType(HospitoEvent.EventType.APPOINTMENT_COMPLETED);
        event.setOccurredAt(LocalDateTime.now());
        kafkaTemplate.send(TOPIC, String.valueOf(event.getAppointmentId()), event);
        log.info("📤 Published APPOINTMENT_COMPLETED event for appointmentId={}", event.getAppointmentId());
    }
}