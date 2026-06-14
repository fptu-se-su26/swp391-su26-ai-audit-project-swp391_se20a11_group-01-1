package com.rms.restaurant_management_system.service;

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
import com.rms.restaurant_management_system.service.impl.ReservationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private RestaurantTableRepository tableRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    private User user;
    private RestaurantTable table;
    private Reservation reservation;
    private ReservationRequest request;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).email("test@example.com").build();
        table = RestaurantTable.builder().id(1L).number(5).capacity(4).status(TableStatus.AVAILABLE).build();
        reservation = Reservation.builder()
                .id(1L)
                .user(user)
                .table(table)
                .reservationTime(LocalDateTime.now().plusDays(1))
                .guestCount(2)
                .status(ReservationStatus.PENDING)
                .build();

        request = ReservationRequest.builder()
                .tableId(1L)
                .reservationTime(LocalDateTime.now().plusDays(1))
                .guestCount(2)
                .build();
    }

    @Test
    void createReservation_Success() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(tableRepository.findById(1L)).thenReturn(Optional.of(table));
        when(reservationRepository.findConflictingReservations(eq(1L), any(), any(), any())).thenReturn(Collections.emptyList());
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        ReservationResponse response = reservationService.createReservation(request, "test@example.com");

        assertNotNull(response);
        assertEquals(ReservationStatus.PENDING, response.getStatus());
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    void createReservation_TableNotAvailable() {
        table.setStatus(TableStatus.OCCUPIED);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(tableRepository.findById(1L)).thenReturn(Optional.of(table));

        assertThrows(BadRequestException.class, () -> reservationService.createReservation(request, "test@example.com"));
    }

    @Test
    void createReservation_GuestCountExceedsCapacity() {
        request.setGuestCount(10);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(tableRepository.findById(1L)).thenReturn(Optional.of(table));

        assertThrows(BadRequestException.class, () -> reservationService.createReservation(request, "test@example.com"));
    }

    @Test
    void createReservation_TimeConflict() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(tableRepository.findById(1L)).thenReturn(Optional.of(table));
        when(reservationRepository.findConflictingReservations(eq(1L), any(), any(), any()))
                .thenReturn(List.of(reservation));

        assertThrows(BadRequestException.class, () -> reservationService.createReservation(request, "test@example.com"));
    }

    @Test
    void getReservationById_AsOwner_Success() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        ReservationResponse response = reservationService.getReservationById(1L, "test@example.com", false);
        assertNotNull(response);
    }

    @Test
    void getReservationById_AsAdmin_Success() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        ReservationResponse response = reservationService.getReservationById(1L, "admin@example.com", true);
        assertNotNull(response);
    }

    @Test
    void getReservationById_AsOtherUser_AccessDenied() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        assertThrows(AccessDeniedException.class, () -> reservationService.getReservationById(1L, "other@example.com", false));
    }

    @Test
    void confirmReservation_Success() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        ReservationResponse response = reservationService.confirmReservation(1L);
        
        assertEquals(ReservationStatus.CONFIRMED, reservation.getStatus());
    }

    @Test
    void confirmReservation_NotPending() {
        reservation.setStatus(ReservationStatus.CONFIRMED);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        assertThrows(BadRequestException.class, () -> reservationService.confirmReservation(1L));
    }

    @Test
    void cancelReservation_AsOwner_Success() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        ReservationResponse response = reservationService.cancelReservation(1L, "test@example.com", false);
        
        assertEquals(ReservationStatus.CANCELLED, reservation.getStatus());
    }
}
