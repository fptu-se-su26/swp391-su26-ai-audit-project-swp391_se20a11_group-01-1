package com.restaurant.management.controller;

import com.restaurant.management.dto.invoice.InvoiceDetailResponse;
import com.restaurant.management.dto.invoice.InvoiceResponse;
import com.restaurant.management.exception.ApiResponse;
import com.restaurant.management.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping("/generate/{paymentId}")
    public ResponseEntity<ApiResponse<InvoiceResponse>> generateInvoice(@PathVariable Long paymentId) {
        InvoiceResponse response = invoiceService.generateInvoice(paymentId);
        return ResponseEntity.ok(ApiResponse.success(response, "Invoice generated successfully"));
    }

    @GetMapping("/{invoiceId}")
    public ResponseEntity<ApiResponse<InvoiceDetailResponse>> getInvoiceById(@PathVariable Long invoiceId) {
        InvoiceDetailResponse response = invoiceService.getInvoiceById(invoiceId);
        return ResponseEntity.ok(ApiResponse.success(response, "Invoice fetched successfully"));
    }

    @GetMapping("/payment/{paymentId}")
    public ResponseEntity<ApiResponse<InvoiceDetailResponse>> getInvoiceByPaymentId(@PathVariable Long paymentId) {
        InvoiceDetailResponse response = invoiceService.getInvoiceByPaymentId(paymentId);
        return ResponseEntity.ok(ApiResponse.success(response, "Invoice fetched successfully"));
    }
}
