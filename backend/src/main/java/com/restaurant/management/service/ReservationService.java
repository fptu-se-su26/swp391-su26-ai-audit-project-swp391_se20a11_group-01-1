package com.restaurant.management.service;

import com.restaurant.management.dto.reservation.CancelReservationRequest;
import com.restaurant.management.dto.reservation.ReservationDetailResponse;
import com.restaurant.management.dto.reservation.ReservationRequest;
import com.restaurant.management.dto.reservation.ReservationResponse;
import com.restaurant.management.dto.reservation.UpdateReservationStatusRequest;

import java.util.List;

public interface ReservationService {
    ReservationResponse createReservation(ReservationRequest request);
    List<ReservationResponse> getMyReservations();
    ReservationDetailResponse getReservationById(Long id);
    void cancelReservation(Long id, CancelReservationRequest request);

    List<ReservationResponse> getAllReservations();
    ReservationResponse updateReservationStatus(Long id, UpdateReservationStatusRequest request);
}
