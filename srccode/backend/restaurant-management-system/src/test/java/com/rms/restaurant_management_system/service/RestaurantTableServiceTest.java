package com.rms.restaurant_management_system.service;

import com.rms.restaurant_management_system.dto.request.RestaurantTableRequest;
import com.rms.restaurant_management_system.dto.response.RestaurantTableResponse;
import com.rms.restaurant_management_system.entity.RestaurantTable;
import com.rms.restaurant_management_system.enums.TableStatus;
import com.rms.restaurant_management_system.exception.BadRequestException;
import com.rms.restaurant_management_system.exception.ResourceNotFoundException;
import com.rms.restaurant_management_system.repository.RestaurantTableRepository;
import com.rms.restaurant_management_system.service.impl.RestaurantTableServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RestaurantTableServiceTest {

    @Mock
    private RestaurantTableRepository tableRepository;

    @InjectMocks
    private RestaurantTableServiceImpl tableService;

    private RestaurantTable table;
    private RestaurantTableRequest request;

    @BeforeEach
    void setUp() {
        table = RestaurantTable.builder()
                .id(1L)
                .number(5)
                .capacity(4)
                .status(TableStatus.AVAILABLE)
                .build();

        request = RestaurantTableRequest.builder()
                .number(5)
                .capacity(4)
                .status(TableStatus.AVAILABLE)
                .build();
    }

    @Test
    void getAllTables_ShouldReturnList() {
        when(tableRepository.findAll()).thenReturn(List.of(table));
        List<RestaurantTableResponse> result = tableService.getAllTables();
        assertEquals(1, result.size());
        assertEquals(5, result.get(0).getNumber());
    }

    @Test
    void getTableById_WhenFound_ShouldReturnTable() {
        when(tableRepository.findById(1L)).thenReturn(Optional.of(table));
        RestaurantTableResponse result = tableService.getTableById(1L);
        assertEquals(5, result.getNumber());
    }

    @Test
    void getTableById_WhenNotFound_ShouldThrowException() {
        when(tableRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> tableService.getTableById(1L));
    }

    @Test
    void createTable_WhenValid_ShouldReturnCreatedTable() {
        when(tableRepository.existsByNumber(5)).thenReturn(false);
        when(tableRepository.save(any(RestaurantTable.class))).thenReturn(table);
        
        RestaurantTableResponse result = tableService.createTable(request);
        
        assertNotNull(result);
        assertEquals(5, result.getNumber());
        verify(tableRepository).save(any(RestaurantTable.class));
    }

    @Test
    void createTable_WhenDuplicateNumber_ShouldThrowException() {
        when(tableRepository.existsByNumber(5)).thenReturn(true);
        assertThrows(BadRequestException.class, () -> tableService.createTable(request));
        verify(tableRepository, never()).save(any());
    }

    @Test
    void updateTable_WhenValid_ShouldReturnUpdatedTable() {
        RestaurantTableRequest updateRequest = RestaurantTableRequest.builder().number(10).capacity(6).build();
        when(tableRepository.findById(1L)).thenReturn(Optional.of(table));
        when(tableRepository.existsByNumberAndIdNot(10, 1L)).thenReturn(false);
        when(tableRepository.save(any(RestaurantTable.class))).thenReturn(table);

        RestaurantTableResponse result = tableService.updateTable(1L, updateRequest);
        
        assertNotNull(result);
        verify(tableRepository).save(table);
    }

    @Test
    void updateTable_WhenDuplicateNumber_ShouldThrowException() {
        RestaurantTableRequest updateRequest = RestaurantTableRequest.builder().number(10).capacity(6).build();
        when(tableRepository.findById(1L)).thenReturn(Optional.of(table));
        when(tableRepository.existsByNumberAndIdNot(10, 1L)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> tableService.updateTable(1L, updateRequest));
        verify(tableRepository, never()).save(any());
    }

    @Test
    void deleteTable_WhenValid_ShouldDelete() {
        when(tableRepository.findById(1L)).thenReturn(Optional.of(table));
        doNothing().when(tableRepository).delete(table);

        assertDoesNotThrow(() -> tableService.deleteTable(1L));
        verify(tableRepository).delete(table);
    }

    @Test
    void deleteTable_WhenConstraintViolation_ShouldThrowException() {
        when(tableRepository.findById(1L)).thenReturn(Optional.of(table));
        doThrow(DataIntegrityViolationException.class).when(tableRepository).delete(table);

        assertThrows(BadRequestException.class, () -> tableService.deleteTable(1L));
    }
}
