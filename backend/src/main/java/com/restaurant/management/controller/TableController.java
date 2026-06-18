package com.restaurant.management.controller;

import com.restaurant.management.dto.reservation.TableResponse;
import com.restaurant.management.exception.ApiResponse;
import com.restaurant.management.service.RestaurantTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tables")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class TableController {

    private final RestaurantTableService tableService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TableResponse>>> getAllTables() {
        return ResponseEntity.ok(ApiResponse.success(tableService.getAllTables(), "Tables fetched successfully"));
    }
}
