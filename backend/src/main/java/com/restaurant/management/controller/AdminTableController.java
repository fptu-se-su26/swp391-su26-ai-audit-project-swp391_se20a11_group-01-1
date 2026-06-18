package com.restaurant.management.controller;

import com.restaurant.management.dto.reservation.TableRequest;
import com.restaurant.management.dto.reservation.TableResponse;
import com.restaurant.management.dto.reservation.UpdateTableStatusRequest;
import com.restaurant.management.exception.ApiResponse;
import com.restaurant.management.service.RestaurantTableService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/tables")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
public class AdminTableController {

    private final RestaurantTableService tableService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TableResponse>>> getAllTables() {
        return ResponseEntity.ok(ApiResponse.success(tableService.getAllTables(), "Tables fetched successfully"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TableResponse>> createTable(@Valid @RequestBody TableRequest request) {
        return ResponseEntity.ok(ApiResponse.success(tableService.createTable(request), "Table created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TableResponse>> updateTable(@PathVariable Long id, @Valid @RequestBody TableRequest request) {
        return ResponseEntity.ok(ApiResponse.success(tableService.updateTable(id, request), "Table updated successfully"));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<TableResponse>> updateTableStatus(@PathVariable Long id, @Valid @RequestBody UpdateTableStatusRequest request) {
        return ResponseEntity.ok(ApiResponse.success(tableService.updateTableStatus(id, request), "Table status updated successfully"));
    }
}
