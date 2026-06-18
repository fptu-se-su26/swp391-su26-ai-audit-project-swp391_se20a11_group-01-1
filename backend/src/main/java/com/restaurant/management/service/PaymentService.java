package com.restaurant.management.service;

import com.restaurant.management.dto.payment.PaymentConfirmRequest;
import com.restaurant.management.dto.payment.PaymentResponse;

import java.util.List;

public interface PaymentService {
    PaymentResponse confirmPayment(Long orderId, PaymentConfirmRequest request);
    List<PaymentResponse> getPaymentsByOrderId(Long orderId);
}
