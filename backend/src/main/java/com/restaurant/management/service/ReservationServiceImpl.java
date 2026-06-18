package com.restaurant.management.service;

import com.restaurant.management.dto.reservation.CancelReservationRequest;
import com.restaurant.management.dto.reservation.ReservationDetailResponse;
import com.restaurant.management.dto.reservation.ReservationRequest;
import com.restaurant.management.dto.reservation.ReservationResponse;
import com.restaurant.management.dto.reservation.TableResponse;
import com.restaurant.management.dto.reservation.UpdateReservationStatusRequest;
import com.restaurant.management.entity.auth.User;
import com.restaurant.management.entity.reservation.Reservation;
import com.restaurant.management.entity.reservation.ReservationStatus;
import com.restaurant.management.entity.reservation.RestaurantTable;
import com.restaurant.management.entity.reservation.TableStatus;
import com.restaurant.management.exception.BadRequestException;
import com.restaurant.management.exception.ResourceNotFoundException;
import com.restaurant.management.repository.ReservationRepository;
import com.restaurant.management.repository.RestaurantTableRepository;
import com.restaurant.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
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
    public ReservationResponse createReservation(ReservationRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User customer = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (request.getReservationTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Reservation time must be in the future");
        }

        RestaurantTable selectedTable = null;
        LocalDateTime startTime = request.getReservationTime();
        LocalDateTime endTime = startTime.plusHours(2);
        LocalDateTime startTimeMinus2Hours = startTime.minusHours(2);
        List<ReservationStatus> activeStatuses = Arrays.asList(ReservationStatus.PENDING, ReservationStatus.CONFIRMED);

        if (request.getTableId() != null) {
            selectedTable = tableRepository.findById(request.getTableId())
                    .filter(t -> t.getDeletedAt() == null)
                    .orElseThrow(() -> new ResourceNotFoundException("Table not found"));

            if (selectedTable.getStatus() == TableStatus.INACTIVE) {
                throw new BadRequestException("Table is inactive");
            }
            if (selectedTable.getCapacity() < request.getGuestCount()) {
                throw new BadRequestException("Party size exceeds table capacity");
            }

            boolean isOverlap = reservationRepository.existsOverlapReservation(selectedTable.getId(), startTimeMinus2Hours, endTime, activeStatuses);
            if (isOverlap) {
                throw new BadRequestException("Table is already reserved for this time");
            }
        } else {
            // Auto assign table
            List<RestaurantTable> availableTables = tableRepository.findAllByDeletedAtIsNull().stream()
                    .filter(t -> t.getStatus() != TableStatus.INACTIVE)
                    .filter(t -> t.getCapacity() >= request.getGuestCount())
                    .collect(Collectors.toList());

            for (RestaurantTable table : availableTables) {
                boolean isOverlap = reservationRepository.existsOverlapReservation(table.getId(), startTimeMinus2Hours, endTime, activeStatuses);
                if (!isOverlap) {
                    selectedTable = table;
                    break;
                }
            }

            if (selectedTable == null) {
                throw new BadRequestException("No available table found for this time and party size");
            }
        }

        Reservation reservation = Reservation.builder()
                .customer(customer)
                .table(selectedTable)
                .reservationTime(request.getReservationTime())
                .guestCount(request.getGuestCount())
                .specialRequest(request.getSpecialRequest())
                .status(ReservationStatus.PENDING)
                .version(0L)
                .build();

        return mapToResponse(reservationRepository.save(reservation));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponse> getMyReservations() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User customer = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return reservationRepository.findByCustomerIdAndDeletedAtIsNull(customer.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ReservationDetailResponse getReservationById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .filter(r -> r.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean isAdminOrStaff = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_STAFF"));

        if (!isAdminOrStaff && !reservation.getCustomer().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Reservation not found or you don't have permission");
        }

        return mapToDetailResponse(reservation);
    }

    @Override
    @Transactional
    public void cancelReservation(Long id, CancelReservationRequest request) {
        Reservation reservation = reservationRepository.findById(id)
                .filter(r -> r.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!reservation.getCustomer().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Reservation not found or you don't have permission");
        }

        if (reservation.getStatus() != ReservationStatus.PENDING && reservation.getStatus() != ReservationStatus.CONFIRMED) {
            throw new BadRequestException("Cannot cancel reservation that is not PENDING or CONFIRMED");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        if (request != null && request.getReason() != null) {
            reservation.setSpecialRequest((reservation.getSpecialRequest() != null ? reservation.getSpecialRequest() + " | " : "") + "Cancel reason: " + request.getReason());
        }

        reservationRepository.save(reservation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponse> getAllReservations() {
        return reservationRepository.findAllByDeletedAtIsNull().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ReservationResponse updateReservationStatus(Long id, UpdateReservationStatusRequest request) {
        Reservation reservation = reservationRepository.findById(id)
                .filter(r -> r.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));

        reservation.setStatus(request.getStatus());
        return mapToResponse(reservationRepository.save(reservation));
    }

    private ReservationResponse mapToResponse(Reservation reservation) {
        return ReservationResponse.builder()
                .id(reservation.getId())
                .customerId(reservation.getCustomer().getId())
                .tableId(reservation.getTable().getId())
                .reservationTime(reservation.getReservationTime())
                .guestCount(reservation.getGuestCount())
                .status(reservation.getStatus())
                .specialRequest(reservation.getSpecialRequest())
                .createdAt(reservation.getCreatedAt())
                .updatedAt(reservation.getUpdatedAt())
                .build();
    }

    private ReservationDetailResponse mapToDetailResponse(Reservation reservation) {
        RestaurantTable table = reservation.getTable();
        TableResponse tableResponse = TableResponse.builder()
                .id(table.getId())
                .tableNumber(table.getTableNumber())
                .capacity(table.getCapacity())
                .status(table.getStatus())
                .location(table.getLocation())
                .createdAt(table.getCreatedAt())
                .updatedAt(table.getUpdatedAt())
                .build();

        return ReservationDetailResponse.builder()
                .id(reservation.getId())
                .customerId(reservation.getCustomer().getId())
                .customerName(reservation.getCustomer().getUsername())
                .table(tableResponse)
                .reservationTime(reservation.getReservationTime())
                .guestCount(reservation.getGuestCount())
                .status(reservation.getStatus())
                .specialRequest(reservation.getSpecialRequest())
                .createdAt(reservation.getCreatedAt())
                .updatedAt(reservation.getUpdatedAt())
                .build();
    }
}
