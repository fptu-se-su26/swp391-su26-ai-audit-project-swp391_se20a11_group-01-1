package com.rms.restaurant_management_system.service.interfaces;

import com.rms.restaurant_management_system.dto.request.RestaurantTableRequest;
import com.rms.restaurant_management_system.dto.response.RestaurantTableResponse;

import java.util.List;

public interface RestaurantTableService {
    List<RestaurantTableResponse> getAllTables();
    RestaurantTableResponse getTableById(Long id);
    RestaurantTableResponse createTable(RestaurantTableRequest request);
    RestaurantTableResponse updateTable(Long id, RestaurantTableRequest request);
    void deleteTable(Long id);
}
