package com.rms.restaurant_management_system.service.interfaces;

import com.rms.restaurant_management_system.dto.request.UpdateOrderItemStatusRequest;
import com.rms.restaurant_management_system.dto.response.KitchenItemResponse;

import java.util.List;

public interface KitchenService {
    List<KitchenItemResponse> getActiveKitchenItems();
    void updateItemStatus(Long orderItemId, UpdateOrderItemStatusRequest request);
}
