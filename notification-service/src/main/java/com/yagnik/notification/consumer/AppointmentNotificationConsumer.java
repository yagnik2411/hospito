package com.yagnik.notification.consumer;

import com.yagnik.notification.event.HospitoEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AppointmentNotificationConsumer {

    @KafkaListener(topics = "appointment-events", groupId = "notification-group")
    public void handleAppointmentEvent(HospitoEvent event) {
        switch (event.getEventType()) {

            case APPOINTMENT_BOOKED -> {
                log.info("═══════════════════════════════════════════════════");
                log.info("📅 APPOINTMENT BOOKED NOTIFICATION");
                log.info("   To:      {}", event.getPatientEmail());
                log.info("   Patient: {}", event.getPatientName());
                log.info("   Doctor:  {}", event.getDoctorName());
                log.info("   Branch:  {}", event.getBranchName());
                log.info("   Time:    {}", event.getAppointmentTime());
                log.info("   Reason:  {}", event.getReason());
                log.info("   MSG:     Your appointment has been booked successfully!");
                log.info("═══════════════════════════════════════════════════");
            }

            case APPOINTMENT_CONFIRMED -> {
                log.info("═══════════════════════════════════════════════════");
                log.info("✅ APPOINTMENT CONFIRMED NOTIFICATION");
                log.info("   To:      {}", event.getPatientEmail());
                log.info("   Patient: {}", event.getPatientName());
                log.info("   Doctor:  {}", event.getDoctorName());
                log.info("   Time:    {}", event.getAppointmentTime());
                log.info("   MSG:     Your appointment has been confirmed. Please arrive 10 minutes early.");
                log.info("═══════════════════════════════════════════════════");
            }

            case APPOINTMENT_COMPLETED -> {
                log.info("═══════════════════════════════════════════════════");
                log.info("🏥 APPOINTMENT COMPLETED NOTIFICATION");
                log.info("   To:      {}", event.getPatientEmail());
                log.info("   Patient: {}", event.getPatientName());
                log.info("   Doctor:  {}", event.getDoctorName());
                log.info("   MSG:     Thank you for visiting Hospito. We hope you feel better soon!");
                log.info("═══════════════════════════════════════════════════");
            }

            default -> log.warn("⚠️ Unknown appointment event type: {}", event.getEventType());
        }
    }
}