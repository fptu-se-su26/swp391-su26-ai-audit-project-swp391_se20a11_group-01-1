package com.rms.restaurant_management_system.service.interfaces;

import com.rms.restaurant_management_system.dto.request.CreateOrderRequest;
import com.rms.restaurant_management_system.dto.request.UpdateOrderStatusRequest;
import com.rms.restaurant_management_system.dto.response.OrderResponse;

import java.util.List;

public interface OrderService {
    OrderResponse createOrderFromCart(String userEmail, CreateOrderRequest request);
    List<OrderResponse> getMyOrders(String userEmail);
    OrderResponse getOrderById(Long id, String userEmail, boolean isAdminOrStaff);
    List<OrderResponse> getAllOrders();
    OrderResponse updateOrderStatus(Long id, UpdateOrderStatusRequest request);
    OrderResponse cancelOrder(Long id, String userEmail, boolean isAdminOrStaff);
}
