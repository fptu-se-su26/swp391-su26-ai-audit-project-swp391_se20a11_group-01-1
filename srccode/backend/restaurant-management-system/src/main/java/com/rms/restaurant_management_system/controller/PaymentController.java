package com.rms.restaurant_management_system.controller;

import com.rms.restaurant_management_system.dto.request.PaymentRequest;
import com.rms.restaurant_management_system.dto.response.PaymentResponse;
import com.rms.restaurant_management_system.service.interfaces.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PaymentResponse> processPayment(@Valid @RequestBody PaymentRequest request,
                                                          Authentication authentication) {
        return ResponseEntity.ok(paymentService.processPayment(request, authentication.getName()));
    }

    @GetMapping("/order/{orderId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PaymentResponse> getPaymentByOrderId(@PathVariable Long orderId,
                                                               Authentication authentication) {
        return ResponseEntity.ok(paymentService.getPaymentByOrderId(orderId, authentication.getName()));
    }
}
