package com.restaurant.management.controller;

import com.restaurant.management.dto.reservation.ReservationResponse;
import com.restaurant.management.dto.reservation.UpdateReservationStatusRequest;
import com.restaurant.management.exception.ApiResponse;
import com.restaurant.management.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/reservations")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
public class AdminReservationController {

    private final ReservationService reservationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ReservationResponse>>> getAllReservations() {
        return ResponseEntity.ok(ApiResponse.success(reservationService.getAllReservations(), "Reservations fetched successfully"));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ReservationResponse>> updateReservationStatus(@PathVariable Long id, @Valid @RequestBody UpdateReservationStatusRequest request) {
        return ResponseEntity.ok(ApiResponse.success(reservationService.updateReservationStatus(id, request), "Reservation status updated successfully"));
    }
}
