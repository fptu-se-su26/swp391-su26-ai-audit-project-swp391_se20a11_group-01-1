package com.restaurant.management.controller;

import com.restaurant.management.dto.reservation.CancelReservationRequest;
import com.restaurant.management.dto.reservation.ReservationDetailResponse;
import com.restaurant.management.dto.reservation.ReservationRequest;
import com.restaurant.management.dto.reservation.ReservationResponse;
import com.restaurant.management.exception.ApiResponse;
import com.restaurant.management.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReservationResponse>> createReservation(@Valid @RequestBody ReservationRequest request) {
        return ResponseEntity.ok(ApiResponse.success(reservationService.createReservation(request), "Reservation created successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ReservationResponse>>> getMyReservations() {
        return ResponseEntity.ok(ApiResponse.success(reservationService.getMyReservations(), "Reservations fetched successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReservationDetailResponse>> getReservationById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(reservationService.getReservationById(id), "Reservation fetched successfully"));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelReservation(@PathVariable Long id, @RequestBody(required = false) CancelReservationRequest request) {
        reservationService.cancelReservation(id, request);
        return ResponseEntity.ok(ApiResponse.success(null, "Reservation cancelled successfully"));
    }
}
