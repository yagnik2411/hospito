package com.yagnik.hospito.billing.service;

import com.yagnik.hospito.appointment.entity.Appointment;
import com.yagnik.hospito.appointment.enums.AppointmentStatus;
import com.yagnik.hospito.appointment.repository.AppointmentRepository;
import com.yagnik.hospito.billing.dto.BillItemRequest;
import com.yagnik.hospito.billing.dto.BillResponse;
import com.yagnik.hospito.billing.dto.CreateBillRequest;
import com.yagnik.hospito.billing.dto.ProcessPaymentRequest;
import com.yagnik.hospito.billing.entity.Bill;
import com.yagnik.hospito.billing.enums.BillStatus;
import com.yagnik.hospito.billing.enums.PaymentMethodType;
import com.yagnik.hospito.billing.repository.BillRepository;
import com.yagnik.hospito.billing.service.impl.BillingServiceImpl;
import com.yagnik.hospito.billing.strategy.PaymentRequest;
import com.yagnik.hospito.billing.strategy.PaymentResult;
import com.yagnik.hospito.billing.strategy.PaymentStrategy;
import com.yagnik.hospito.billing.strategy.factory.PaymentStrategyFactory;
import com.yagnik.hospito.branch.entity.Branch;
import com.yagnik.hospito.common.exception.BusinessRuleException;
import com.yagnik.hospito.common.exception.ResourceNotFoundException;
import com.yagnik.hospito.doctor.entity.Doctor;
import com.yagnik.hospito.patient.entity.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BillingService Unit Tests")
class BillingServiceTest {

    @Mock private BillRepository billRepository;
    @Mock private AppointmentRepository appointmentRepository;
    @Mock private PaymentStrategyFactory strategyFactory;
    @Mock private PaymentStrategy mockStrategy;

    @InjectMocks
    private BillingServiceImpl billingService;

    private Branch mockBranch;
    private Patient mockPatient;
    private Doctor mockDoctor;
    private Appointment mockAppointment;
    private Bill mockBill;
    private CreateBillRequest createRequest;

    @BeforeEach
    void setUp() {
        mockBranch = Branch.builder()
                .id(1L).name("Hospito Surat").isActive(true)
                .build();

        mockPatient = Patient.builder()
                .id(1L).name("Rahul Mehta").isActive(true)
                .visitedBranches(new java.util.HashSet<>())
                .build();

        mockDoctor = Doctor.builder()
                .id(1L).name("Dr. Aanya Sharma")
                .specialization("Cardiologist")
                .build();

        mockAppointment = Appointment.builder()
                .id(1L)
                .status(AppointmentStatus.COMPLETED)
                .appointmentTime(LocalDateTime.now().minusDays(1))
                .reason("Chest pain")
                .patient(mockPatient)
                .doctor(mockDoctor)
                .branch(mockBranch)
                .build();

        mockBill = Bill.builder()
                .id(1L)
                .appointment(mockAppointment)
                .patient(mockPatient)
                .branch(mockBranch)
                .totalAmount(new BigDecimal("1000.00"))
                .discountAmount(BigDecimal.ZERO)
                .taxAmount(BigDecimal.ZERO)
                .finalAmount(new BigDecimal("1000.00"))
                .patientPayableAmount(new BigDecimal("1000.00"))
                .insuranceCoveredAmount(BigDecimal.ZERO)
                .status(BillStatus.PENDING)
                .items(new ArrayList<>())
                .build();

        BillItemRequest item = new BillItemRequest();
        item.setDescription("Consultation Fee");
        item.setQuantity(1);
        item.setUnitPrice(new BigDecimal("1000.00"));

        createRequest = new CreateBillRequest();
        createRequest.setAppointmentId(1L);
        createRequest.setItems(List.of(item));
    }

    // ── CREATE BILL ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("createBill - completed appointment - should return BillResponse")
    void createBill_withCompletedAppointment_shouldReturnBillResponse() {
        // Arrange
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(mockAppointment));
        when(billRepository.existsByAppointmentId(1L)).thenReturn(false);
        when(billRepository.save(any(Bill.class))).thenReturn(mockBill);

        // Act
        BillResponse response = billingService.createBill(createRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(BillStatus.PENDING);
        assertThat(response.getPatientName()).isEqualTo("Rahul Mehta");
        verify(billRepository, times(1)).save(any(Bill.class));
    }

    @Test
    @DisplayName("createBill - pending appointment - should throw BusinessRuleException")
    void createBill_withPendingAppointment_shouldThrowBusinessRuleException() {
        // Arrange
        mockAppointment.setStatus(AppointmentStatus.PENDING);
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(mockAppointment));

        // Act + Assert
        assertThatThrownBy(() -> billingService.createBill(createRequest))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("COMPLETED");

        verify(billRepository, never()).save(any());
    }

    @Test
    @DisplayName("createBill - duplicate bill - should throw BusinessRuleException")
    void createBill_withDuplicateBill_shouldThrowBusinessRuleException() {
        // Arrange
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(mockAppointment));
        when(billRepository.existsByAppointmentId(1L)).thenReturn(true);

        // Act + Assert
        assertThatThrownBy(() -> billingService.createBill(createRequest))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Bill already exists");

        verify(billRepository, never()).save(any());
    }

    @Test
    @DisplayName("createBill - appointment not found - should throw ResourceNotFoundException")
    void createBill_withNonExistentAppointment_shouldThrowResourceNotFoundException() {
        // Arrange
        when(appointmentRepository.findById(1L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> billingService.createBill(createRequest))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── PROCESS PAYMENT ──────────────────────────────────────────────────────

    @Test
    @DisplayName("processPayment - CASH - should return PAID status")
    void processPayment_withCash_shouldReturnPaidStatus() {
        // Arrange
        ProcessPaymentRequest request = new ProcessPaymentRequest();
        request.setPaymentMethod(PaymentMethodType.CASH);

        PaymentResult cashResult = PaymentResult.builder()
                .success(true)
                .resultingStatus(BillStatus.PAID)
                .amountPaid(new BigDecimal("1000.00"))
                .insuranceCovered(BigDecimal.ZERO)
                .patientPaid(new BigDecimal("1000.00"))
                .transactionReference("CASH-ABC12345")
                .message("Cash payment recorded successfully.")
                .build();

        when(billRepository.findById(1L)).thenReturn(Optional.of(mockBill));
        when(strategyFactory.getStrategy(PaymentMethodType.CASH)).thenReturn(mockStrategy);
        when(mockStrategy.processPayment(any(PaymentRequest.class))).thenReturn(cashResult);
        when(billRepository.save(any(Bill.class))).thenReturn(mockBill);

        // Act
        BillResponse response = billingService.processPayment(1L, request);

        // Assert
        assertThat(response).isNotNull();
        verify(strategyFactory, times(1)).getStrategy(PaymentMethodType.CASH);
        verify(mockStrategy, times(1)).processPayment(any(PaymentRequest.class));
        verify(billRepository, times(1)).save(any(Bill.class));
    }

    @Test
    @DisplayName("processPayment - already PAID - should throw BusinessRuleException")
    void processPayment_withAlreadyPaidBill_shouldThrowBusinessRuleException() {
        // Arrange
        mockBill.setStatus(BillStatus.PAID);
        ProcessPaymentRequest request = new ProcessPaymentRequest();
        request.setPaymentMethod(PaymentMethodType.CASH);

        when(billRepository.findById(1L)).thenReturn(Optional.of(mockBill));

        // Act + Assert
        assertThatThrownBy(() -> billingService.processPayment(1L, request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("already PAID");

        verify(strategyFactory, never()).getStrategy(any());
    }

    @Test
    @DisplayName("processPayment - INSURANCE - should return PARTIALLY_PAID status")
    void processPayment_withInsurance_shouldReturnPartiallyPaidStatus() {
        // Arrange
        ProcessPaymentRequest request = new ProcessPaymentRequest();
        request.setPaymentMethod(PaymentMethodType.INSURANCE);

        PaymentResult insuranceResult = PaymentResult.builder()
                .success(true)
                .resultingStatus(BillStatus.PARTIALLY_PAID)
                .amountPaid(new BigDecimal("1000.00"))
                .insuranceCovered(new BigDecimal("800.00"))
                .patientPaid(new BigDecimal("200.00"))
                .transactionReference("INS-LIC-2024-001")
                .message("Insurance covers ₹800.00. Patient pays ₹200.00.")
                .build();

        when(billRepository.findById(1L)).thenReturn(Optional.of(mockBill));
        when(strategyFactory.getStrategy(PaymentMethodType.INSURANCE)).thenReturn(mockStrategy);
        when(mockStrategy.processPayment(any(PaymentRequest.class))).thenReturn(insuranceResult);
        when(billRepository.save(any(Bill.class))).thenReturn(mockBill);

        // Act
        BillResponse response = billingService.processPayment(1L, request);

        // Assert
        assertThat(response).isNotNull();
        verify(mockStrategy, times(1)).processPayment(any(PaymentRequest.class));
    }

    // ── WAIVE BILL ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("waiveBill - PENDING bill - should return WAIVED status")
    void waiveBill_withPendingBill_shouldReturnWaivedStatus() {
        // Arrange
        Bill waivedBill = Bill.builder()
                .id(1L).appointment(mockAppointment)
                .patient(mockPatient).branch(mockBranch)
                .totalAmount(new BigDecimal("1000.00"))
                .discountAmount(BigDecimal.ZERO)
                .taxAmount(BigDecimal.ZERO)
                .finalAmount(new BigDecimal("1000.00"))
                .insuranceCoveredAmount(BigDecimal.ZERO)
                .patientPayableAmount(new BigDecimal("1000.00"))
                .status(BillStatus.WAIVED)
                .notes("Waived for underprivileged patient")
                .items(new ArrayList<>())
                .build();

        when(billRepository.findById(1L)).thenReturn(Optional.of(mockBill));
        when(billRepository.save(any(Bill.class))).thenReturn(waivedBill);

        // Act
        BillResponse response = billingService.waiveBill(1L, "Waived for underprivileged patient");

        // Assert
        assertThat(response.getStatus()).isEqualTo(BillStatus.WAIVED);
        assertThat(response.getNotes()).isEqualTo("Waived for underprivileged patient");
    }

    @Test
    @DisplayName("waiveBill - already PAID - should throw BusinessRuleException")
    void waiveBill_withAlreadyPaidBill_shouldThrowBusinessRuleException() {
        // Arrange
        mockBill.setStatus(BillStatus.PAID);
        when(billRepository.findById(1L)).thenReturn(Optional.of(mockBill));

        // Act + Assert
        assertThatThrownBy(() -> billingService.waiveBill(1L, "notes"))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Cannot waive");
    }
}