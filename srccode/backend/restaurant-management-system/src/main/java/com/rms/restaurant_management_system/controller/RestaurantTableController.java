package com.rms.restaurant_management_system.controller;

import com.rms.restaurant_management_system.dto.request.RestaurantTableRequest;
import com.rms.restaurant_management_system.dto.response.RestaurantTableResponse;
import com.rms.restaurant_management_system.service.interfaces.RestaurantTableService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tables")
@RequiredArgsConstructor
public class RestaurantTableController {

    private final RestaurantTableService tableService;

    @GetMapping
    public ResponseEntity<List<RestaurantTableResponse>> getAllTables() {
        return ResponseEntity.ok(tableService.getAllTables());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantTableResponse> getTableById(@PathVariable Long id) {
        return ResponseEntity.ok(tableService.getTableById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<RestaurantTableResponse> createTable(@Valid @RequestBody RestaurantTableRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tableService.createTable(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<RestaurantTableResponse> updateTable(@PathVariable Long id,
                                                               @Valid @RequestBody RestaurantTableRequest request) {
        return ResponseEntity.ok(tableService.updateTable(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<Void> deleteTable(@PathVariable Long id) {
        tableService.deleteTable(id);
        return ResponseEntity.noContent().build();
    }
}
