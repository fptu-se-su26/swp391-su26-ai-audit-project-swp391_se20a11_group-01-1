package com.restaurant.management.service;

import com.restaurant.management.dto.kitchen.KitchenOrderResponse;
import com.restaurant.management.dto.kitchen.KitchenUpdateOrderStatusRequest;

import java.util.List;

public interface KitchenOrderService {
    List<KitchenOrderResponse> getActiveOrders();
    KitchenOrderResponse getOrderById(Long id);
    KitchenOrderResponse updateOrderStatus(Long id, KitchenUpdateOrderStatusRequest request);
}
