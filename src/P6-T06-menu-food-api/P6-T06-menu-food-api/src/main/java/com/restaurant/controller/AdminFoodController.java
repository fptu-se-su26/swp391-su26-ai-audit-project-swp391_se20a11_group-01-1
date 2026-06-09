package com.restaurant.controller;

import com.restaurant.dto.food.FoodAvailabilityRequest;
import com.restaurant.dto.food.FoodRequest;
import com.restaurant.dto.food.FoodResponse;
import com.restaurant.service.FoodService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/foods")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminFoodController {

    private final FoodService foodService;

    @PostMapping
    public ResponseEntity<FoodResponse> createFood(@Valid @RequestBody FoodRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(foodService.createFood(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FoodResponse> updateFood(
            @PathVariable Long id,
            @Valid @RequestBody FoodRequest request
    ) {
        return ResponseEntity.ok(foodService.updateFood(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFood(@PathVariable Long id) {
        foodService.deleteFood(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/availability")
    public ResponseEntity<FoodResponse> updateAvailability(
            @PathVariable Long id,
            @Valid @RequestBody FoodAvailabilityRequest request
    ) {
        return ResponseEntity.ok(foodService.updateAvailability(id, request.getAvailable()));
    }
}
