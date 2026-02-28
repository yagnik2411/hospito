package com.yagnik.hospito.billing.service.impl;

import com.yagnik.hospito.appointment.entity.Appointment;
import com.yagnik.hospito.appointment.enums.AppointmentStatus;
import com.yagnik.hospito.appointment.repository.AppointmentRepository;
import com.yagnik.hospito.billing.dto.*;
import com.yagnik.hospito.billing.entity.Bill;
import com.yagnik.hospito.billing.entity.BillItem;
import com.yagnik.hospito.billing.enums.BillStatus;
import com.yagnik.hospito.billing.producer.BillingEventProducer;
import com.yagnik.hospito.billing.repository.BillRepository;
import com.yagnik.hospito.billing.service.BillingService;
import com.yagnik.hospito.billing.strategy.PaymentRequest;
import com.yagnik.hospito.billing.strategy.PaymentResult;
import com.yagnik.hospito.billing.strategy.PaymentStrategy;
import com.yagnik.hospito.billing.strategy.factory.PaymentStrategyFactory;
import com.yagnik.hospito.common.event.HospitoEvent;
import com.yagnik.hospito.common.exception.BusinessRuleException;
import com.yagnik.hospito.common.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BillingServiceImpl implements BillingService {

    private final BillRepository billRepository;
    private final AppointmentRepository appointmentRepository;
    private final PaymentStrategyFactory strategyFactory;
    private final BillingEventProducer billingEventProducer;

    @Override
    @Transactional
    public BillResponse createBill(CreateBillRequest request) {

        // Appointment must exist
        Appointment appointment = appointmentRepository
                .findById(request.getAppointmentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Appointment", request.getAppointmentId()));

        // Appointment must be COMPLETED to bill
        if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
            throw new BusinessRuleException(
                    "Can only create bill for COMPLETED appointments. " +
                            "Current status: " + appointment.getStatus());
        }

        // Prevent duplicate billing
        if (billRepository.existsByAppointmentId(request.getAppointmentId())) {
            throw new BusinessRuleException(
                    "Bill already exists for appointment id: "
                            + request.getAppointmentId());
        }

        // Build items and compute totals
        List<BillItem> items = request.getItems().stream()
                .map(itemReq -> {
                    BigDecimal totalPrice = itemReq.getUnitPrice()
                            .multiply(BigDecimal.valueOf(itemReq.getQuantity()))
                            .setScale(2, RoundingMode.HALF_UP);
                    return BillItem.builder()
                            .description(itemReq.getDescription())
                            .quantity(itemReq.getQuantity())
                            .unitPrice(itemReq.getUnitPrice())
                            .totalPrice(totalPrice)
                            .build();
                })
                .toList();

        BigDecimal totalAmount = items.stream()
                .map(BillItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discount = request.getDiscountAmount() != null
                ? request.getDiscountAmount()
                : BigDecimal.ZERO;

        BigDecimal taxAmount = BigDecimal.ZERO;
        if (request.getTaxPercent() != null &&
                request.getTaxPercent().compareTo(BigDecimal.ZERO) > 0) {
            taxAmount = totalAmount
                    .subtract(discount)
                    .multiply(request.getTaxPercent())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }

        BigDecimal finalAmount = totalAmount
                .subtract(discount)
                .add(taxAmount)
                .setScale(2, RoundingMode.HALF_UP);

        Bill bill = Bill.builder()
                .appointment(appointment)
                .patient(appointment.getPatient())
                .branch(appointment.getBranch())
                .totalAmount(totalAmount)
                .discountAmount(discount)
                .taxAmount(taxAmount)
                .finalAmount(finalAmount)
                .patientPayableAmount(finalAmount)
                .status(BillStatus.PENDING)
                .notes(request.getNotes())
                .build();

        // Link items to bill
        items.forEach(item -> item.setBill(bill));
        bill.getItems().addAll(items);

        return mapToResponse(billRepository.save(bill), null);
    }

    @Override
    public BillResponse getBillById(Long id) {
        return mapToResponse(findBillById(id), null);
    }

    @Override
    public BillResponse getBillByAppointment(Long appointmentId) {
        Bill bill = billRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Bill for appointment id " + appointmentId + " not found."));
        return mapToResponse(bill, null);
    }

    @Override
    public List<BillResponse> getBillsByPatient(Long patientId) {
        return billRepository.findAllByPatientIdOrderByCreatedAtDesc(patientId)
                .stream()
                .map(b -> mapToResponse(b, null))
                .toList();
    }

    @Override
    public List<BillResponse> getBillsByBranch(Long branchId, BillStatus status) {
        List<Bill> bills = status != null
                ? billRepository.findAllByBranchIdAndStatusOrderByCreatedAtDesc(
                        branchId, status)
                : billRepository.findAllByBranchIdOrderByCreatedAtDesc(branchId);

        return bills.stream().map(b -> mapToResponse(b, null)).toList();
    }

    @Override
    @Transactional
    public BillResponse processPayment(Long billId, ProcessPaymentRequest request) {
        Bill bill = findBillById(billId);

        if (bill.getStatus() == BillStatus.PAID ||
                bill.getStatus() == BillStatus.WAIVED) {
            throw new BusinessRuleException(
                    "Bill is already " + bill.getStatus() + ". Cannot process payment.");
        }

        // Get strategy via factory
        PaymentStrategy strategy = strategyFactory.getStrategy(
                request.getPaymentMethod());

        PaymentRequest paymentRequest = PaymentRequest.builder()
                .bill(bill)
                .amountToPay(bill.getFinalAmount())
                .transactionReference(request.getTransactionReference())
                .insurance(bill.getPatient().getInsurance())
                .build();

        PaymentResult result = strategy.processPayment(paymentRequest);

        // Apply result to bill
        bill.setPaymentMethod(request.getPaymentMethod());
        bill.setStatus(result.getResultingStatus());
        bill.setInsuranceCoveredAmount(result.getInsuranceCovered());
        bill.setPatientPayableAmount(result.getPatientPaid());
        bill.setTransactionReference(result.getTransactionReference());

        Bill paid = billRepository.save(bill);

        billingEventProducer.publishBillPaid(HospitoEvent.builder()
                .billId(paid.getId())
                .patientName(paid.getPatient().getName())
                .patientEmail(paid.getPatient().getUser().getEmail())
                .amountPaid(result.getAmountPaid())
                .paymentMethod(request.getPaymentMethod().name())
                .transactionReference(result.getTransactionReference())
                .build());

        return mapToResponse(paid, result.getMessage());
    }

    @Override
    @Transactional
    public BillResponse waiveBill(Long billId, String notes) {
        Bill bill = findBillById(billId);

        if (bill.getStatus() == BillStatus.PAID) {
            throw new BusinessRuleException(
                    "Cannot waive a bill that is already PAID.");
        }

        bill.setStatus(BillStatus.WAIVED);
        bill.setNotes(notes);

        return mapToResponse(billRepository.save(bill), "Bill waived by admin.");
    }

    // ── Private helpers ──────────────────────────────────────────────────

    private Bill findBillById(Long id) {
        return billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bill", id));
    }

    private BillResponse mapToResponse(Bill bill, String paymentMessage) {
        List<BillItemResponse> itemResponses = bill.getItems().stream()
                .map(item -> BillItemResponse.builder()
                        .id(item.getId())
                        .description(item.getDescription())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .totalPrice(item.getTotalPrice())
                        .build())
                .toList();

        return BillResponse.builder()
                .id(bill.getId())
                .appointmentId(bill.getAppointment().getId())
                .patientId(bill.getPatient().getId())
                .patientName(bill.getPatient().getName())
                .branchId(bill.getBranch().getId())
                .branchName(bill.getBranch().getName())
                .totalAmount(bill.getTotalAmount())
                .discountAmount(bill.getDiscountAmount())
                .taxAmount(bill.getTaxAmount())
                .finalAmount(bill.getFinalAmount())
                .paymentMethod(bill.getPaymentMethod())
                .status(bill.getStatus())
                .insuranceCoveredAmount(bill.getInsuranceCoveredAmount())
                .patientPayableAmount(bill.getPatientPayableAmount())
                .transactionReference(bill.getTransactionReference())
                .notes(bill.getNotes())
                .paymentMessage(paymentMessage)
                .items(itemResponses)
                .createdAt(bill.getCreatedAt())
                .build();
    }
}