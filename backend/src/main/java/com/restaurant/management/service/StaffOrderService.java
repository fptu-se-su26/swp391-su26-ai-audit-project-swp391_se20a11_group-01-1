package com.restaurant.management.service;

import com.restaurant.management.dto.staff.StaffCreateOrderRequest;
import com.restaurant.management.dto.staff.StaffOrderResponse;
import com.restaurant.management.dto.staff.StaffUpdateOrderStatusRequest;

import java.util.List;

public interface StaffOrderService {
    StaffOrderResponse createOrder(StaffCreateOrderRequest request);
    List<StaffOrderResponse> getAllOrders();
    StaffOrderResponse getOrderById(Long id);
    StaffOrderResponse updateOrderStatus(Long id, StaffUpdateOrderStatusRequest request);
}
