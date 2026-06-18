package com.restaurant.management.controller;

import com.restaurant.management.dto.payment.PaymentConfirmRequest;
import com.restaurant.management.dto.payment.PaymentResponse;
import com.restaurant.management.exception.ApiResponse;
import com.restaurant.management.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/{orderId}/confirm")
    public ResponseEntity<ApiResponse<PaymentResponse>> confirmPayment(
            @PathVariable Long orderId,
            @Valid @RequestBody PaymentConfirmRequest request) {
        PaymentResponse response = paymentService.confirmPayment(orderId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Payment confirmed successfully"));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPaymentsByOrderId(
            @PathVariable Long orderId) {
        List<PaymentResponse> responses = paymentService.getPaymentsByOrderId(orderId);
        return ResponseEntity.ok(ApiResponse.success(responses, "Payments fetched successfully"));
    }
}
