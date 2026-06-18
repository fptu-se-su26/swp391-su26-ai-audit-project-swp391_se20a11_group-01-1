package com.restaurant.management.service;

import com.restaurant.management.dto.reservation.CancelReservationRequest;
import com.restaurant.management.dto.reservation.ReservationRequest;
import com.restaurant.management.dto.reservation.ReservationResponse;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceImplTest {

    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private RestaurantTableRepository tableRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    private User customer;
    private RestaurantTable table;
    private Reservation reservation;

    @BeforeEach
    void setUp() {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getName()).thenReturn("customer1");
        SecurityContextHolder.setContext(securityContext);

        customer = new User();
        customer.setId(1L);
        customer.setUsername("customer1");

        table = new RestaurantTable();
        table.setId(10L);
        table.setCapacity(4);
        table.setStatus(TableStatus.AVAILABLE);

        reservation = new Reservation();
        reservation.setId(100L);
        reservation.setCustomer(customer);
        reservation.setTable(table);
        reservation.setStatus(ReservationStatus.PENDING);
    }

    @Test
    void testCreateReservation_SelectedTable_Success() {
        ReservationRequest request = new ReservationRequest();
        request.setTableId(10L);
        request.setGuestCount(4);
        request.setReservationTime(LocalDateTime.now().plusDays(1));

        when(userRepository.findByUsername("customer1")).thenReturn(Optional.of(customer));
        when(tableRepository.findById(10L)).thenReturn(Optional.of(table));
        when(reservationRepository.existsOverlapReservation(eq(10L), any(LocalDateTime.class), any(LocalDateTime.class), anyList())).thenReturn(false);
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(i -> {
            Reservation r = i.getArgument(0);
            r.setId(100L);
            return r;
        });

        ReservationResponse response = reservationService.createReservation(request);

        assertEquals(100L, response.getId());
        assertEquals(10L, response.getTableId());
        assertEquals(ReservationStatus.PENDING, response.getStatus());
    }

    @Test
    void testCreateReservation_AutoAssignTable_Success() {
        ReservationRequest request = new ReservationRequest();
        request.setGuestCount(2);
        request.setReservationTime(LocalDateTime.now().plusDays(1));

        when(userRepository.findByUsername("customer1")).thenReturn(Optional.of(customer));
        when(tableRepository.findAllByDeletedAtIsNull()).thenReturn(Collections.singletonList(table));
        when(reservationRepository.existsOverlapReservation(eq(10L), any(LocalDateTime.class), any(LocalDateTime.class), anyList())).thenReturn(false);
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(i -> {
            Reservation r = i.getArgument(0);
            r.setId(101L);
            return r;
        });

        ReservationResponse response = reservationService.createReservation(request);

        assertEquals(10L, response.getTableId());
    }

    @Test
    void testCreateReservation_OverlapFails() {
        ReservationRequest request = new ReservationRequest();
        request.setTableId(10L);
        request.setGuestCount(2);
        request.setReservationTime(LocalDateTime.now().plusDays(1));

        when(userRepository.findByUsername("customer1")).thenReturn(Optional.of(customer));
        when(tableRepository.findById(10L)).thenReturn(Optional.of(table));
        when(reservationRepository.existsOverlapReservation(eq(10L), any(LocalDateTime.class), any(LocalDateTime.class), anyList())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> reservationService.createReservation(request));
    }

    @Test
    void testCreateReservation_PartySizeExceedsFails() {
        ReservationRequest request = new ReservationRequest();
        request.setTableId(10L);
        request.setGuestCount(5); // Table capacity is 4
        request.setReservationTime(LocalDateTime.now().plusDays(1));

        when(userRepository.findByUsername("customer1")).thenReturn(Optional.of(customer));
        when(tableRepository.findById(10L)).thenReturn(Optional.of(table));

        assertThrows(BadRequestException.class, () -> reservationService.createReservation(request));
    }

    @Test
    void testCreateReservation_PastTimeFails() {
        ReservationRequest request = new ReservationRequest();
        request.setGuestCount(2);
        request.setReservationTime(LocalDateTime.now().minusDays(1));

        when(userRepository.findByUsername("customer1")).thenReturn(Optional.of(customer));

        assertThrows(BadRequestException.class, () -> reservationService.createReservation(request));
    }

    @Test
    void testCancelReservation_Success() {
        when(userRepository.findByUsername("customer1")).thenReturn(Optional.of(customer));
        when(reservationRepository.findById(100L)).thenReturn(Optional.of(reservation));

        CancelReservationRequest request = new CancelReservationRequest();
        request.setReason("Change of plans");

        reservationService.cancelReservation(100L, request);

        assertEquals(ReservationStatus.CANCELLED, reservation.getStatus());
        verify(reservationRepository).save(reservation);
    }

    @Test
    void testCancelReservation_OtherCustomer_Fails() {
        User otherCustomer = new User();
        otherCustomer.setId(2L);
        otherCustomer.setUsername("customer2");

        reservation.setCustomer(otherCustomer);

        when(userRepository.findByUsername("customer1")).thenReturn(Optional.of(customer));
        when(reservationRepository.findById(100L)).thenReturn(Optional.of(reservation));

        assertThrows(ResourceNotFoundException.class, () -> reservationService.cancelReservation(100L, new CancelReservationRequest()));
    }

    @Test
    void testUpdateReservationStatus_Success() {
        when(reservationRepository.findById(100L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any())).thenReturn(reservation);

        UpdateReservationStatusRequest request = new UpdateReservationStatusRequest();
        request.setStatus(ReservationStatus.CONFIRMED);

        ReservationResponse response = reservationService.updateReservationStatus(100L, request);

        assertEquals(ReservationStatus.CONFIRMED, response.getStatus());
    }
}
