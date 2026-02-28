package com.yagnik.hospito.common.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HospitoEvent {

    // ── EVENT TYPE ────────────────────────────────────────────────────────────
    private EventType eventType;

    // ── APPOINTMENT FIELDS ────────────────────────────────────────────────────
    private Long appointmentId;
    private String patientName;
    private String patientEmail;
    private String doctorName;
    private String branchName;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime appointmentTime;
    private String reason;
    private String notes;

    // ── BILLING FIELDS ────────────────────────────────────────────────────────
    private Long billId;
    private BigDecimal amountPaid;
    private String paymentMethod;
    private String transactionReference;

    // ── METADATA ──────────────────────────────────────────────────────────────
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime occurredAt;

    public enum EventType {
        APPOINTMENT_BOOKED,
        APPOINTMENT_CONFIRMED,
        APPOINTMENT_COMPLETED,
        BILL_PAID
    }
}