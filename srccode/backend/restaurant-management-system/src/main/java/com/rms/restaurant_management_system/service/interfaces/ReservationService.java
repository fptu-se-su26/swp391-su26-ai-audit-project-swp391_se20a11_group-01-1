package com.rms.restaurant_management_system.service.interfaces;

import com.rms.restaurant_management_system.dto.request.ReservationRequest;
import com.rms.restaurant_management_system.dto.response.ReservationResponse;

import java.util.List;

public interface ReservationService {
    ReservationResponse createReservation(ReservationRequest request, String userEmail);
    List<ReservationResponse> getMyReservations(String userEmail);
    ReservationResponse getReservationById(Long id, String userEmail, boolean isAdminOrStaff);
    List<ReservationResponse> getAllReservations();
    ReservationResponse confirmReservation(Long id);
    ReservationResponse cancelReservation(Long id, String userEmail, boolean isAdminOrStaff);
}
