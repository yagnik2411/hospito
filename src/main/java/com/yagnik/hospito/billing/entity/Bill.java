package com.yagnik.hospito.billing.entity;

import com.yagnik.hospito.appointment.entity.Appointment;
import com.yagnik.hospito.billing.enums.BillStatus;
import com.yagnik.hospito.billing.enums.PaymentMethodType;
import com.yagnik.hospito.branch.entity.Branch;
import com.yagnik.hospito.common.entity.AuditableEntity;
import com.yagnik.hospito.patient.entity.Patient;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bills")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Bill extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", nullable = false, unique = true)
    private Appointment appointment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal finalAmount;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private PaymentMethodType paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BillStatus status;

    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal insuranceCoveredAmount = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal patientPayableAmount;

    @Column(length = 100)
    private String transactionReference;

    @Column(length = 500)
    private String notes;

    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<BillItem> items = new ArrayList<>();
}