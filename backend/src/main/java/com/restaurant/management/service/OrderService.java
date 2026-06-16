package com.restaurant.management.service;

import com.restaurant.management.dto.order.CancelOrderRequest;
import com.restaurant.management.dto.order.CheckoutRequest;
import com.restaurant.management.dto.order.OrderDetailResponse;
import com.restaurant.management.dto.order.OrderResponse;

import java.util.List;

public interface OrderService {
    OrderDetailResponse checkout(CheckoutRequest request);
    List<OrderResponse> getMyOrders();
    OrderDetailResponse getMyOrder(Long id);
    OrderResponse cancelOrder(Long id, CancelOrderRequest request);
}
