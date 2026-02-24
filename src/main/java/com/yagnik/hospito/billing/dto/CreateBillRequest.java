package com.yagnik.hospito.billing.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateBillRequest {

    @NotNull(message = "Appointment ID is mandatory")
    private Long appointmentId;

    @NotEmpty(message = "At least one bill item is required")
    @Valid
    private List<BillItemRequest> items;

    // Optional discount
    private BigDecimal discountAmount;

    // Tax rate in percent e.g. 18.0 for 18% GST
    private BigDecimal taxPercent;

    private String notes;
}