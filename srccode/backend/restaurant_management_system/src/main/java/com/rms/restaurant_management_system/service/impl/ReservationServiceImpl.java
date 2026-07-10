package com.rms.restaurant_management_system.service.impl;

import com.rms.restaurant_management_system.dto.request.ReservationPreOrderItemRequest;
import com.rms.restaurant_management_system.dto.request.ReservationRequest;
import com.rms.restaurant_management_system.dto.request.UpdateReservationStatusRequest;
import com.rms.restaurant_management_system.dto.response.ReservationPreOrderItemResponse;
import com.rms.restaurant_management_system.dto.response.ReservationResponse;
import com.rms.restaurant_management_system.entity.Reservation;
import com.rms.restaurant_management_system.entity.ReservationPreOrderItem;
import com.rms.restaurant_management_system.entity.User;
import com.rms.restaurant_management_system.enums.ReservationStatus;
import com.rms.restaurant_management_system.repository.ReservationRepository;
import com.rms.restaurant_management_system.repository.UserRepository;
import com.rms.restaurant_management_system.service.interfaces.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ReservationResponse createReservation(ReservationRequest request) {
        User user = null;

        if (request.getUserId() != null) {
            user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }

        Reservation reservation = Reservation.builder()
                .reservationCode(generateReservationCode())
                .customerName(request.getCustomerName().trim())
                .phone(request.getPhone().trim())
                .reservationDate(request.getReservationDate())
                .reservationTime(request.getReservationTime())
                .guests(request.getGuests())
                .status(ReservationStatus.PENDING)
                .note(request.getNote())
                .user(user)
                .preOrderItems(new ArrayList<>())
                .build();

        if (request.getPreOrderItems() != null) {
            for (ReservationPreOrderItemRequest itemRequest : request.getPreOrderItems()) {
                if (itemRequest.getQuantity() == null || itemRequest.getQuantity() <= 0) {
                    continue;
                }

                BigDecimal unitPrice = itemRequest.getUnitPrice() == null ? BigDecimal.ZERO : itemRequest.getUnitPrice();
                BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(itemRequest.getQuantity()));

                ReservationPreOrderItem item = ReservationPreOrderItem.builder()
                        .reservation(reservation)
                        .foodId(itemRequest.getFoodId())
                        .foodName(itemRequest.getFoodName())
                        .quantity(itemRequest.getQuantity())
                        .unitPrice(unitPrice)
                        .subtotal(subtotal)
                        .build();

                reservation.getPreOrderItems().add(item);
            }
        }

        Reservation savedReservation = reservationRepository.save(reservation);

        return mapToResponse(savedReservation);
    }

    @Override
    public List<ReservationResponse> getAllReservations() {
        return reservationRepository.findAllByOrderByReservationDateDescReservationTimeDesc()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public ReservationResponse getReservationById(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        return mapToResponse(reservation);
    }

    @Override
    public List<ReservationResponse> getReservationsByCustomer(Long userId) {
        return reservationRepository.findByUserUserIdOrderByReservationDateDescReservationTimeDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<ReservationResponse> getReservationsByStatus(String status) {
        ReservationStatus reservationStatus;

        try {
            reservationStatus = ReservationStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new RuntimeException("Invalid reservation status: " + status);
        }

        return reservationRepository.findByStatusOrderByReservationDateAscReservationTimeAsc(reservationStatus)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public ReservationResponse updateReservationStatus(Long reservationId, UpdateReservationStatusRequest request) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        reservation.setStatus(request.getStatus());

        if (request.getAssignedTable() != null) {
            reservation.setAssignedTable(request.getAssignedTable().isBlank() ? null : request.getAssignedTable());
        }

        if (request.getStatus() == ReservationStatus.PENDING) {
            reservation.setAssignedTable(null);
        }

        Reservation savedReservation = reservationRepository.save(reservation);

        return mapToResponse(savedReservation);
    }

    @Override
    @Transactional
    public void deleteReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        reservationRepository.delete(reservation);
    }

    private ReservationResponse mapToResponse(Reservation reservation) {
        List<ReservationPreOrderItemResponse> items = reservation.getPreOrderItems()
                .stream()
                .map(item -> new ReservationPreOrderItemResponse(
                        item.getPreOrderItemId(),
                        item.getFoodId(),
                        item.getFoodName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getSubtotal()
                ))
                .toList();

        return new ReservationResponse(
                reservation.getReservationId(),
                reservation.getReservationCode(),
                reservation.getUser() == null ? null : reservation.getUser().getUserId(),
                reservation.getCustomerName(),
                reservation.getPhone(),
                reservation.getReservationDate(),
                reservation.getReservationTime(),
                reservation.getGuests(),
                reservation.getStatus(),
                reservation.getAssignedTable(),
                reservation.getNote(),
                reservation.getCreatedAt(),
                reservation.getUpdatedAt(),
                items
        );
    }

    private String generateReservationCode() {
        String timestamp = java.time.LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        int random = (int) (Math.random() * 9000) + 1000;

        return "RES-" + timestamp + "-" + random;
    }
}
