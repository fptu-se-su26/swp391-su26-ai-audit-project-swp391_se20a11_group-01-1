package com.rms.restaurant_management_system.service.impl;

import com.rms.restaurant_management_system.dto.request.CheckInReservationRequest;
import com.rms.restaurant_management_system.dto.request.ReservationItemRequest;
import com.rms.restaurant_management_system.dto.request.ReservationRequest;
import com.rms.restaurant_management_system.dto.request.UpdateReservationStatusRequest;
import com.rms.restaurant_management_system.dto.response.ReservationItemResponse;
import com.rms.restaurant_management_system.dto.response.ReservationResponse;
import com.rms.restaurant_management_system.entity.Food;
import com.rms.restaurant_management_system.entity.Order;
import com.rms.restaurant_management_system.entity.OrderItem;
import com.rms.restaurant_management_system.entity.Reservation;
import com.rms.restaurant_management_system.entity.ReservationItem;
import com.rms.restaurant_management_system.entity.RestaurantTable;
import com.rms.restaurant_management_system.entity.User;
import com.rms.restaurant_management_system.enums.OrderStatus;
import com.rms.restaurant_management_system.enums.ReservationStatus;
import com.rms.restaurant_management_system.enums.TableStatus;
import com.rms.restaurant_management_system.repository.FoodRepository;
import com.rms.restaurant_management_system.repository.OrderRepository;
import com.rms.restaurant_management_system.repository.ReservationRepository;
import com.rms.restaurant_management_system.repository.RestaurantTableRepository;
import com.rms.restaurant_management_system.repository.UserRepository;
import com.rms.restaurant_management_system.service.interfaces.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final FoodRepository foodRepository;
    private final OrderRepository orderRepository;
    private final RestaurantTableRepository restaurantTableRepository;

    @Override
    @Transactional
    public ReservationResponse createReservation(ReservationRequest request) {
        if (request.getReservationDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Reservation date cannot be in the past");
        }

        User user = null;

        if (request.getUserId() != null) {
            user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }

        Reservation reservation = Reservation.builder()
                .reservationCode(generateReservationCode())
                .user(user)
                .customerName(request.getCustomerName())
                .customerPhone(request.getCustomerPhone())
                .customerEmail(
                        request.getCustomerEmail() != null && !request.getCustomerEmail().isBlank()
                                ? request.getCustomerEmail()
                                : user != null ? user.getEmail() : null
                )
                .reservationDate(request.getReservationDate())
                .reservationTime(request.getReservationTime())
                .numberOfGuests(request.getNumberOfGuests())
                .note(request.getNote())
                .assignedTable(null)
                .status(ReservationStatus.PENDING)
                .preOrderTotal(BigDecimal.ZERO)
                .items(new ArrayList<>())
                .build();

        BigDecimal preOrderTotal = BigDecimal.ZERO;

        if (request.getItems() != null && !request.getItems().isEmpty()) {
            for (ReservationItemRequest itemRequest : request.getItems()) {
                Food food = foodRepository.findById(itemRequest.getFoodId())
                        .orElseThrow(() -> new RuntimeException("Food not found: " + itemRequest.getFoodId()));

                if (!food.getIsAvailable()) {
                    throw new RuntimeException("Food is not available: " + food.getFoodName());
                }

                BigDecimal unitPrice = food.getPrice();
                BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(itemRequest.getQuantity()));

                ReservationItem reservationItem = ReservationItem.builder()
                        .reservation(reservation)
                        .foodId(food.getFoodId())
                        .foodName(food.getFoodName())
                        .unitPrice(unitPrice)
                        .quantity(itemRequest.getQuantity())
                        .subtotal(subtotal)
                        .imageUrl(food.getImageUrl())
                        .emoji(food.getEmoji())
                        .build();

                reservation.getItems().add(reservationItem);
                preOrderTotal = preOrderTotal.add(subtotal);
            }
        }

        reservation.setPreOrderTotal(preOrderTotal);

        Reservation savedReservation = reservationRepository.save(reservation);

        return mapToResponse(savedReservation);
    }

    @Override
    public List<ReservationResponse> getAllReservations() {
        return reservationRepository.findAllByOrderByCreatedAtDesc()
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
        return reservationRepository.findByUserUserIdOrderByCreatedAtDesc(userId)
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

        return reservationRepository.findByStatusOrderByCreatedAtDesc(reservationStatus)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public ReservationResponse updateReservationStatus(
            Long reservationId,
            UpdateReservationStatusRequest request
    ) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        validateStatusTransition(reservation.getStatus(), request.getStatus());

        reservation.setStatus(request.getStatus());

        if (request.getStatus() == ReservationStatus.CANCELLED
                || request.getStatus() == ReservationStatus.NO_SHOW) {
            reservation.setAssignedTable(null);
        }

        Reservation savedReservation = reservationRepository.save(reservation);

        return mapToResponse(savedReservation);
    }

    @Override
    @Transactional
    public ReservationResponse checkInReservation(
            Long reservationId,
            CheckInReservationRequest request
    ) {
        Reservation reservation = reservationRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
            throw new RuntimeException("Only CONFIRMED reservations can be checked in");
        }

        if (request.getAssignedTable() == null || request.getAssignedTable().isBlank()) {
            throw new RuntimeException("Assigned table is required");
        }

        RestaurantTable table = restaurantTableRepository.findByTableNameForUpdate(request.getAssignedTable().trim())
                .orElseThrow(() -> new RuntimeException("Table not found: " + request.getAssignedTable()));

        if (table.getIsActive() == null || !table.getIsActive()) {
            throw new RuntimeException("Table is inactive");
        }

        if (table.getStatus() != TableStatus.EMPTY) {
            throw new RuntimeException("Assigned table must be EMPTY");
        }

        reservation.setAssignedTable(table.getTableName());
        reservation.setStatus(ReservationStatus.SEATED);

        Order createdOrder = null;

        if (reservation.getItems() != null && !reservation.getItems().isEmpty()) {
            createdOrder = createOrderFromReservation(reservation, table);

            table.setStatus(TableStatus.OCCUPIED);
            table.setCurrentOrderCode(createdOrder.getOrderCode());
            table.setReservedBy(reservation.getCustomerName());
        } else {
            table.setStatus(TableStatus.OCCUPIED);
            table.setCurrentOrderCode(null);
            table.setReservedBy(reservation.getCustomerName());
        }

        restaurantTableRepository.save(table);

        Reservation savedReservation = reservationRepository.save(reservation);

        return mapToResponse(savedReservation);
    }

    @Override
    @Transactional
    public void cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        if (reservation.getStatus() != ReservationStatus.PENDING
                && reservation.getStatus() != ReservationStatus.CONFIRMED) {
            throw new RuntimeException(
                    "Only PENDING or CONFIRMED reservations can be cancelled"
            );
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setAssignedTable(null);

        reservationRepository.save(reservation);
    }

    private Order createOrderFromReservation(Reservation reservation, RestaurantTable table) {
        Order order = Order.builder()
                .orderCode(generateOrderCode())
                .status(OrderStatus.CONFIRMED)
                .totalAmount(
                        reservation.getPreOrderTotal() != null
                                ? reservation.getPreOrderTotal()
                                : BigDecimal.ZERO
                )
                .note(buildReservationOrderNote(reservation))
                .tableId(table.getTableId())
                .tableName(table.getTableName())
                .customerName(reservation.getCustomerName())
                .customerPhone(reservation.getCustomerPhone())
                .user(reservation.getUser())
                .items(new ArrayList<>())
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (ReservationItem reservationItem : reservation.getItems()) {
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .foodId(reservationItem.getFoodId())
                    .foodName(reservationItem.getFoodName())
                    .unitPrice(reservationItem.getUnitPrice())
                    .quantity(reservationItem.getQuantity())
                    .subtotal(reservationItem.getSubtotal())
                    .imageUrl(reservationItem.getImageUrl())
                    .emoji(reservationItem.getEmoji())
                    .build();

            order.getItems().add(orderItem);
            totalAmount = totalAmount.add(reservationItem.getSubtotal());
        }

        order.setTotalAmount(totalAmount);

        return orderRepository.save(order);
    }

    private String buildReservationOrderNote(Reservation reservation) {
        StringBuilder note = new StringBuilder();

        note.append("Pre-order từ đặt bàn ")
                .append(reservation.getReservationCode());

        if (reservation.getNote() != null && !reservation.getNote().isBlank()) {
            note.append(" | Ghi chú đặt bàn: ")
                    .append(reservation.getNote());
        }

        return note.toString();
    }

    private void validateStatusTransition(
            ReservationStatus currentStatus,
            ReservationStatus newStatus
    ) {
        if (currentStatus == ReservationStatus.CANCELLED) {
            throw new RuntimeException("Cancelled reservation cannot be updated");
        }

        if (currentStatus == ReservationStatus.COMPLETED) {
            throw new RuntimeException("Completed reservation cannot be updated");
        }

        if (currentStatus == newStatus) {
            return;
        }

        boolean valid = switch (currentStatus) {
            case PENDING -> newStatus == ReservationStatus.CONFIRMED
                    || newStatus == ReservationStatus.CANCELLED;
            case CONFIRMED -> newStatus == ReservationStatus.SEATED
                    || newStatus == ReservationStatus.CANCELLED
                    || newStatus == ReservationStatus.NO_SHOW;
            case SEATED -> newStatus == ReservationStatus.COMPLETED;
            case COMPLETED, CANCELLED, NO_SHOW -> false;
        };

        if (!valid) {
            throw new RuntimeException(
                    "Invalid reservation status transition from "
                            + currentStatus
                            + " to "
                            + newStatus
            );
        }
    }

    private ReservationResponse mapToResponse(Reservation reservation) {
        List<ReservationItemResponse> itemResponses = reservation.getItems()
                .stream()
                .map(item -> new ReservationItemResponse(
                        item.getReservationItemId(),
                        item.getFoodId(),
                        item.getFoodName(),
                        item.getUnitPrice(),
                        item.getQuantity(),
                        item.getSubtotal(),
                        item.getImageUrl(),
                        item.getEmoji()
                ))
                .toList();

        Long userId = reservation.getUser() != null
                ? reservation.getUser().getUserId()
                : null;

        String username = reservation.getUser() != null
                ? reservation.getUser().getUsername()
                : null;

        return new ReservationResponse(
                reservation.getReservationId(),
                reservation.getReservationCode(),
                userId,
                username,
                reservation.getCustomerName(),
                reservation.getCustomerPhone(),
                reservation.getCustomerEmail(),
                reservation.getReservationDate(),
                reservation.getReservationTime(),
                reservation.getNumberOfGuests(),
                reservation.getStatus(),
                reservation.getNote(),
                reservation.getAssignedTable(),
                reservation.getPreOrderTotal(),
                reservation.getCreatedAt(),
                reservation.getUpdatedAt(),
                itemResponses
        );
    }

    private String generateReservationCode() {
        String timestamp = java.time.LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        int random = (int) (Math.random() * 9000) + 1000;

        return "RES-" + timestamp + "-" + random;
    }

    private String generateOrderCode() {
        String timestamp = java.time.LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        int random = (int) (Math.random() * 9000) + 1000;

        return "ORD-RES-" + timestamp + "-" + random;
    }
}
