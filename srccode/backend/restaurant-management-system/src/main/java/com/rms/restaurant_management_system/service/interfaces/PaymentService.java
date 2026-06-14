package com.rms.restaurant_management_system.service.interfaces;

import com.rms.restaurant_management_system.dto.request.PaymentRequest;
import com.rms.restaurant_management_system.dto.response.PaymentResponse;

public interface PaymentService {
    PaymentResponse processPayment(PaymentRequest request, String userEmail);
    PaymentResponse getPaymentByOrderId(Long orderId, String userEmail);
}
