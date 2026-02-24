package com.yagnik.hospito.billing.service;

import com.yagnik.hospito.billing.dto.*;
import com.yagnik.hospito.billing.enums.BillStatus;

import java.util.List;

public interface BillingService {
    BillResponse createBill(CreateBillRequest request);
    BillResponse getBillById(Long id);
    BillResponse getBillByAppointment(Long appointmentId);
    List<BillResponse> getBillsByPatient(Long patientId);
    List<BillResponse> getBillsByBranch(Long branchId, BillStatus status);
    BillResponse processPayment(Long billId, ProcessPaymentRequest request);
    BillResponse waiveBill(Long billId, String notes);
}