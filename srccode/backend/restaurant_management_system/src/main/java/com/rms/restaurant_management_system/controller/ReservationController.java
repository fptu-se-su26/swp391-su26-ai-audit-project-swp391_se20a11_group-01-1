package com.rms.restaurant_management_system.controller;

import com.rms.restaurant_management_system.dto.request.ReservationRequest;
import com.rms.restaurant_management_system.dto.request.UpdateReservationStatusRequest;
import com.rms.restaurant_management_system.dto.response.ReservationResponse;
import com.rms.restaurant_management_system.service.interfaces.ReservationService;
import com.rms.restaurant_management_system.dto.request.CheckInReservationRequest;
import com.rms.restaurant_management_system.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER','STAFF','ADMIN')")
    public ReservationResponse createReservation(@Valid @RequestBody ReservationRequest request,
                                                 @AuthenticationPrincipal User actor) {
        if (isCustomer(actor)) {
            request.setUserId(actor.getUserId());
        }
        return reservationService.createReservation(request);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public List<ReservationResponse> getAllReservations() {
        return reservationService.getAllReservations();
    }

    @GetMapping("/{reservationId}")
    @PreAuthorize("@domainAuthorization.canAccessReservation(#reservationId, authentication)")
    public ReservationResponse getReservationById(@PathVariable Long reservationId) {
        return reservationService.getReservationById(reservationId);
    }

    @GetMapping("/customer/{userId}")
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public List<ReservationResponse> getReservationsByCustomer(@PathVariable Long userId) {
        return reservationService.getReservationsByCustomer(userId);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<ReservationResponse> getMyReservations(@AuthenticationPrincipal User actor) {
        return reservationService.getReservationsByCustomer(actor.getUserId());
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public List<ReservationResponse> getReservationsByStatus(@PathVariable String status) {
        return reservationService.getReservationsByStatus(status);
    }

    @PutMapping("/{reservationId}/status")
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public ReservationResponse updateReservationStatus(
            @PathVariable Long reservationId,
            @Valid @RequestBody UpdateReservationStatusRequest request
    ) {
        return reservationService.updateReservationStatus(reservationId, request);
    }

    @DeleteMapping("/{reservationId}")
    @PreAuthorize("@domainAuthorization.canAccessReservation(#reservationId, authentication)")
    public String cancelReservation(@PathVariable Long reservationId) {
        reservationService.cancelReservation(reservationId);
        return "Reservation cancelled successfully";
    }
    @PutMapping("/{reservationId}/check-in")
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
public ReservationResponse checkInReservation(
        @PathVariable Long reservationId,
        @Valid @RequestBody CheckInReservationRequest request
) {
    return reservationService.checkInReservation(reservationId, request);
}

    private boolean isCustomer(User user) {
        return user != null && "CUSTOMER".equalsIgnoreCase(user.getRole().getRoleName());
    }
}
