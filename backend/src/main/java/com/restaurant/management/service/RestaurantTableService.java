package com.restaurant.management.service;

import com.restaurant.management.dto.reservation.TableRequest;
import com.restaurant.management.dto.reservation.TableResponse;
import com.restaurant.management.dto.reservation.UpdateTableStatusRequest;

import java.util.List;

public interface RestaurantTableService {
    TableResponse createTable(TableRequest request);
    TableResponse updateTable(Long id, TableRequest request);
    TableResponse updateTableStatus(Long id, UpdateTableStatusRequest request);
    List<TableResponse> getAllTables();
    TableResponse getTableById(Long id);
}
