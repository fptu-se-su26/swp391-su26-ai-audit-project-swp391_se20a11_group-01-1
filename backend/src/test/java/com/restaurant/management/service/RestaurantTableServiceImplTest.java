package com.restaurant.management.service;

import com.restaurant.management.dto.reservation.TableRequest;
import com.restaurant.management.dto.reservation.TableResponse;
import com.restaurant.management.dto.reservation.UpdateTableStatusRequest;
import com.restaurant.management.entity.reservation.RestaurantTable;
import com.restaurant.management.entity.reservation.TableStatus;
import com.restaurant.management.exception.BadRequestException;
import com.restaurant.management.repository.RestaurantTableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestaurantTableServiceImplTest {

    @Mock
    private RestaurantTableRepository tableRepository;

    @InjectMocks
    private RestaurantTableServiceImpl tableService;

    private RestaurantTable table;

    @BeforeEach
    void setUp() {
        table = new RestaurantTable();
        table.setId(1L);
        table.setTableNumber("T01");
        table.setCapacity(4);
        table.setStatus(TableStatus.AVAILABLE);
    }

    @Test
    void testCreateTable_Success() {
        TableRequest request = new TableRequest();
        request.setTableNumber("T02");
        request.setCapacity(2);

        when(tableRepository.findByTableNumberAndDeletedAtIsNull("T02")).thenReturn(Optional.empty());
        when(tableRepository.save(any(RestaurantTable.class))).thenAnswer(inv -> {
            RestaurantTable t = inv.getArgument(0);
            t.setId(2L);
            return t;
        });

        TableResponse response = tableService.createTable(request);

        assertEquals(2L, response.getId());
        assertEquals("T02", response.getTableNumber());
        assertEquals(2, response.getCapacity());
        verify(tableRepository).save(any(RestaurantTable.class));
    }

    @Test
    void testCreateTable_DuplicateNumber_Fails() {
        TableRequest request = new TableRequest();
        request.setTableNumber("T01");
        request.setCapacity(2);

        when(tableRepository.findByTableNumberAndDeletedAtIsNull("T01")).thenReturn(Optional.of(table));

        assertThrows(BadRequestException.class, () -> tableService.createTable(request));
    }

    @Test
    void testUpdateTableStatus_Success() {
        UpdateTableStatusRequest request = new UpdateTableStatusRequest();
        request.setStatus(TableStatus.INACTIVE);

        when(tableRepository.findById(1L)).thenReturn(Optional.of(table));
        when(tableRepository.save(any(RestaurantTable.class))).thenReturn(table);

        TableResponse response = tableService.updateTableStatus(1L, request);

        assertEquals(TableStatus.INACTIVE, response.getStatus());
    }
}
