package com.rms.restaurant_management_system.service;

import com.rms.restaurant_management_system.dto.request.CheckInReservationRequest;
import com.rms.restaurant_management_system.dto.request.MergeTableRequest;
import com.rms.restaurant_management_system.dto.request.TransferTableRequest;
import com.rms.restaurant_management_system.entity.Reservation;
import com.rms.restaurant_management_system.entity.RestaurantTable;
import com.rms.restaurant_management_system.enums.ReservationStatus;
import com.rms.restaurant_management_system.enums.TableStatus;
import com.rms.restaurant_management_system.repository.*;
import com.rms.restaurant_management_system.service.impl.ReservationServiceImpl;
import com.rms.restaurant_management_system.service.impl.RestaurantTableServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationTableInvariantTest {
    @Mock ReservationRepository reservationRepository;
    @Mock UserRepository userRepository;
    @Mock FoodRepository foodRepository;
    @Mock OrderRepository orderRepository;
    @Mock RestaurantTableRepository tableRepository;
    @InjectMocks ReservationServiceImpl reservationService;
    @InjectMocks RestaurantTableServiceImpl tableService;

    @Test
    void cancellationUsesLockedReservationAndReleasesSlot() {
        Reservation reservation = Reservation.builder().reservationId(10L).status(ReservationStatus.CONFIRMED)
                .assignedTable("T1").assignedTableId(1L).items(new ArrayList<>()).build();
        when(reservationRepository.findByReservationId(10L)).thenReturn(Optional.of(reservation));

        reservationService.cancelReservation(10L);

        verify(reservationRepository).findByReservationId(10L);
        verify(reservationRepository).save(reservation);
        org.assertj.core.api.Assertions.assertThat(reservation.getAssignedTable()).isNull();
        org.assertj.core.api.Assertions.assertThat(reservation.getAssignedTableId()).isNull();
        org.assertj.core.api.Assertions.assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
    }

    @Test
    void reservationAlreadyConvertedToOrderFailsBeforeTableMutation() {
        Reservation reservation = Reservation.builder().reservationId(10L).status(ReservationStatus.CONFIRMED)
                .createdOrderId(99L).assignedTable("T1").items(new ArrayList<>()).build();
        when(reservationRepository.findByReservationId(10L)).thenReturn(Optional.of(reservation));
        CheckInReservationRequest request = new CheckInReservationRequest();
        request.setAssignedTable("T1");

        assertThatThrownBy(() -> reservationService.checkInReservation(10L, request))
                .hasMessageContaining("already been converted");
        verifyNoInteractions(tableRepository);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void cannotMergeTableIntoItself() {
        MergeTableRequest request = new MergeTableRequest();
        request.setTargetTableId(1L);
        assertThatThrownBy(() -> tableService.mergeTables(1L, request))
                .hasMessageContaining("must be different");
        verifyNoInteractions(tableRepository);
    }

    @Test
    void cannotTransferOrderFromMergedGroup() {
        RestaurantTable source = table(1L, "T1", TableStatus.OCCUPIED);
        source.setMergedWith("T3");
        RestaurantTable target = table(2L, "T2", TableStatus.EMPTY);
        when(tableRepository.findByTableIdForUpdate(1L)).thenReturn(Optional.of(source));
        when(tableRepository.findByTableIdForUpdate(2L)).thenReturn(Optional.of(target));
        TransferTableRequest request = new TransferTableRequest();
        request.setTargetTableId(2L);

        assertThatThrownBy(() -> tableService.transferTable(1L, request))
                .hasMessageContaining("Split merged tables");
        verifyNoInteractions(orderRepository);
    }

    @Test
    void cannotSplitOccupiedMergedGroup() {
        RestaurantTable source = table(1L, "T1", TableStatus.OCCUPIED);
        source.setMergedWith("T2");
        when(tableRepository.findByTableIdForUpdate(1L)).thenReturn(Optional.of(source));

        assertThatThrownBy(() -> tableService.splitTable(1L))
                .hasMessageContaining("occupied or reserved");
        verify(tableRepository, never()).save(any());
    }

    private RestaurantTable table(Long id, String name, TableStatus status) {
        return RestaurantTable.builder().tableId(id).tableName(name).capacity(4).originalCapacity(4)
                .status(status).isActive(true).build();
    }
}
