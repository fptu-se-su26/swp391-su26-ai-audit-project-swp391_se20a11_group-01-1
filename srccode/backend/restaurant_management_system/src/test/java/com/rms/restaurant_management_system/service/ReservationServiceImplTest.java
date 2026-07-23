package com.rms.restaurant_management_system.service;

import com.rms.restaurant_management_system.dto.request.*;
import com.rms.restaurant_management_system.entity.*;
import com.rms.restaurant_management_system.enums.*;
import com.rms.restaurant_management_system.repository.*;
import com.rms.restaurant_management_system.service.impl.ReservationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceImplTest {
    @Mock ReservationRepository reservationRepository;
    @Mock UserRepository userRepository;
    @Mock FoodRepository foodRepository;
    @Mock OrderRepository orderRepository;
    @Mock RestaurantTableRepository tableRepository;
    ReservationServiceImpl service;

    @BeforeEach void setUp() { service = new ReservationServiceImpl(reservationRepository, userRepository, foodRepository, orderRepository, tableRepository); }

    @Test
    void createCalculatesPreorderFromCurrentFoodPrice() {
        ReservationRequest request = validRequest();
        ReservationItemRequest item = new ReservationItemRequest(); item.setFoodId(1L); item.setQuantity(3);
        request.setItems(List.of(item));
        when(foodRepository.findById(1L)).thenReturn(Optional.of(Food.builder().foodId(1L).foodName("Soup").price(new BigDecimal("12000")).isAvailable(true).build()));
        when(reservationRepository.save(any())).thenAnswer(i -> { Reservation r=i.getArgument(0); r.setReservationId(9L); return r; });
        var response = service.createReservation(request);
        assertEquals(new BigDecimal("36000"), response.getPreOrderTotal());
        assertEquals(ReservationStatus.PENDING, response.getStatus());
        assertEquals(1, response.getItems().size());
    }

    @Test
    void createRejectsPastDateMissingFoodAndUnavailableFood() {
        ReservationRequest past = validRequest(); past.setReservationDate(LocalDate.now().minusDays(1));
        assertThrows(RuntimeException.class, () -> service.createReservation(past));

        ReservationRequest missing = validRequest(); missing.setItems(List.of(item(99L, 1)));
        when(foodRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.createReservation(missing));

        ReservationRequest unavailable = validRequest(); unavailable.setItems(List.of(item(1L, 1)));
        when(foodRepository.findById(1L)).thenReturn(Optional.of(Food.builder().foodId(1L).foodName("X").price(BigDecimal.ONE).isAvailable(false).build()));
        assertThrows(RuntimeException.class, () -> service.createReservation(unavailable));
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void stateMachineAcceptsPendingToConfirmedAndRejectsSkippingToCompleted() {
        Reservation reservation = reservation(1L, ReservationStatus.PENDING, new ArrayList<>());
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(reservation)).thenReturn(reservation);
        UpdateReservationStatusRequest confirmed = new UpdateReservationStatusRequest(); confirmed.setStatus(ReservationStatus.CONFIRMED);
        service.updateReservationStatus(1L, confirmed);
        assertEquals(ReservationStatus.CONFIRMED, reservation.getStatus());

        reservation.setStatus(ReservationStatus.PENDING);
        UpdateReservationStatusRequest completed = new UpdateReservationStatusRequest(); completed.setStatus(ReservationStatus.COMPLETED);
        assertThrows(RuntimeException.class, () -> service.updateReservationStatus(1L, completed));
    }

    @Test
    void checkInWithPreorderCreatesOneConfirmedOrderAndOccupiesTable() {
        ReservationItem reservationItem = ReservationItem.builder().foodId(1L).foodName("Soup")
                .unitPrice(new BigDecimal("10")).quantity(2).subtotal(new BigDecimal("20")).build();
        Reservation reservation = reservation(1L, ReservationStatus.CONFIRMED, new ArrayList<>(List.of(reservationItem)));
        reservationItem.setReservation(reservation);
        RestaurantTable table = RestaurantTable.builder().tableId(2L).tableName("T2").capacity(4).status(TableStatus.EMPTY).isActive(true).build();
        when(reservationRepository.findByReservationId(1L)).thenReturn(Optional.of(reservation));
        when(tableRepository.findByTableNameForUpdate("T2")).thenReturn(Optional.of(table));
        when(orderRepository.save(any())).thenAnswer(i -> { Order o=i.getArgument(0); o.setOrderId(8L); return o; });
        when(reservationRepository.save(reservation)).thenReturn(reservation);
        CheckInReservationRequest request = new CheckInReservationRequest(); request.setAssignedTable(" T2 ");
        service.checkInReservation(1L, request);
        assertEquals(ReservationStatus.SEATED, reservation.getStatus());
        assertEquals(TableStatus.OCCUPIED, table.getStatus());
        assertNotNull(table.getCurrentOrderCode());
        verify(orderRepository, times(1)).save(any());
        verify(tableRepository).save(table);
    }

    @Test
    void checkInRejectsWrongReservationStateAndBusyTableWithoutWrites() {
        Reservation reservation = reservation(1L, ReservationStatus.PENDING, new ArrayList<>());
        when(reservationRepository.findByReservationId(1L)).thenReturn(Optional.of(reservation));
        CheckInReservationRequest request = new CheckInReservationRequest(); request.setAssignedTable("T2");
        assertThrows(RuntimeException.class, () -> service.checkInReservation(1L, request));

        reservation.setStatus(ReservationStatus.CONFIRMED);
        RestaurantTable busy = RestaurantTable.builder().tableId(2L).tableName("T2").status(TableStatus.OCCUPIED).isActive(true).build();
        when(tableRepository.findByTableNameForUpdate("T2")).thenReturn(Optional.of(busy));
        assertThrows(RuntimeException.class, () -> service.checkInReservation(1L, request));
        verify(orderRepository, never()).save(any());
        verify(tableRepository, never()).save(any());
    }

    private ReservationRequest validRequest() { ReservationRequest r=new ReservationRequest(); r.setReservationDate(LocalDate.now().plusDays(1)); r.setReservationTime("18:00"); r.setNumberOfGuests(2); r.setCustomerName("Lan"); r.setCustomerPhone("0900"); return r; }
    private ReservationItemRequest item(Long id, int qty) { ReservationItemRequest r=new ReservationItemRequest(); r.setFoodId(id); r.setQuantity(qty); return r; }
    private Reservation reservation(Long id, ReservationStatus status, List<ReservationItem> items) { return Reservation.builder().reservationId(id).reservationCode("RSV-1").reservationDate(LocalDate.now().plusDays(1)).reservationTime("18:00").numberOfGuests(2).customerName("Lan").customerPhone("0900").status(status).preOrderTotal(BigDecimal.ZERO).items(items).build(); }
}
