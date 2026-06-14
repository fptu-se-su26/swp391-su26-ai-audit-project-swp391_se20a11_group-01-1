package com.rms.restaurant_management_system.service.impl;

import com.rms.restaurant_management_system.dto.request.ReservationRequest;
import com.rms.restaurant_management_system.dto.response.ReservationResponse;
import com.rms.restaurant_management_system.entity.Reservation;
import com.rms.restaurant_management_system.entity.RestaurantTable;
import com.rms.restaurant_management_system.entity.User;
import com.rms.restaurant_management_system.enums.ReservationStatus;
import com.rms.restaurant_management_system.enums.TableStatus;
import com.rms.restaurant_management_system.exception.BadRequestException;
import com.rms.restaurant_management_system.exception.ResourceNotFoundException;
import com.rms.restaurant_management_system.repository.ReservationRepository;
import com.rms.restaurant_management_system.repository.RestaurantTableRepository;
import com.rms.restaurant_management_system.repository.UserRepository;
import com.rms.restaurant_management_system.service.interfaces.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final RestaurantTableRepository tableRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ReservationResponse createReservation(ReservationRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        RestaurantTable table = tableRepository.findById(request.getTableId())
                .orElseThrow(() -> new ResourceNotFoundException("Table not found"));

        if (table.getStatus() != TableStatus.AVAILABLE) {
            throw new BadRequestException("Table is not available");
        }

        if (request.getGuestCount() > table.getCapacity()) {
            throw new BadRequestException("Guest count exceeds table capacity");
        }

        LocalDateTime startTime = request.getReservationTime().minusHours(2);
        LocalDateTime endTime = request.getReservationTime().plusHours(2);
        List<ReservationStatus> blockingStatuses = List.of(ReservationStatus.PENDING, ReservationStatus.CONFIRMED);

        List<Reservation> conflicts = reservationRepository.findConflictingReservations(
                table.getId(), startTime, endTime, blockingStatuses);

        if (!conflicts.isEmpty()) {
            throw new BadRequestException("Table is already booked around the requested time");
        }

        Reservation reservation = Reservation.builder()
                .user(user)
                .table(table)
                .reservationTime(request.getReservationTime())
                .guestCount(request.getGuestCount())
                .status(ReservationStatus.PENDING)
                .note(request.getNote())
                .build();

        Reservation savedReservation = reservationRepository.save(reservation);
        return mapToResponse(savedReservation);
    }

    @Override
    public List<ReservationResponse> getMyReservations(String userEmail) {
        return reservationRepository.findByUserEmail(userEmail).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ReservationResponse getReservationById(Long id, String userEmail, boolean isAdminOrStaff) {
        Reservation reservation = getReservationEntity(id);
        
        if (!isAdminOrStaff && !reservation.getUser().getEmail().equals(userEmail)) {
            throw new AccessDeniedException("You do not have permission to view this reservation");
        }
        return mapToResponse(reservation);
    }

    @Override
    public List<ReservationResponse> getAllReservations() {
        return reservationRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ReservationResponse confirmReservation(Long id) {
        Reservation reservation = getReservationEntity(id);
        
        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new BadRequestException("Only PENDING reservations can be confirmed");
        }

        reservation.setStatus(ReservationStatus.CONFIRMED);
        Reservation savedReservation = reservationRepository.save(reservation);
        return mapToResponse(savedReservation);
    }

    @Override
    @Transactional
    public ReservationResponse cancelReservation(Long id, String userEmail, boolean isAdminOrStaff) {
        Reservation reservation = getReservationEntity(id);
        
        if (!isAdminOrStaff && !reservation.getUser().getEmail().equals(userEmail)) {
            throw new AccessDeniedException("You do not have permission to cancel this reservation");
        }

        if (reservation.getStatus() == ReservationStatus.CANCELLED || reservation.getStatus() == ReservationStatus.COMPLETED) {
            throw new BadRequestException("Cannot cancel a reservation that is already completed or cancelled");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        Reservation savedReservation = reservationRepository.save(reservation);
        return mapToResponse(savedReservation);
    }

    private Reservation getReservationEntity(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + id));
    }

    private ReservationResponse mapToResponse(Reservation reservation) {
        return ReservationResponse.builder()
                .id(reservation.getId())
                .userId(reservation.getUser().getId())
                .userEmail(reservation.getUser().getEmail())
                .tableId(reservation.getTable().getId())
                .tableNumber(reservation.getTable().getNumber())
                .reservationTime(reservation.getReservationTime())
                .guestCount(reservation.getGuestCount())
                .status(reservation.getStatus())
                .note(reservation.getNote())
                .createdAt(reservation.getCreatedAt())
                .build();
    }
}
