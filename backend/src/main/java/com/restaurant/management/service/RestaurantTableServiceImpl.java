package com.restaurant.management.service;

import com.restaurant.management.dto.reservation.TableRequest;
import com.restaurant.management.dto.reservation.TableResponse;
import com.restaurant.management.dto.reservation.UpdateTableStatusRequest;
import com.restaurant.management.entity.reservation.RestaurantTable;
import com.restaurant.management.exception.BadRequestException;
import com.restaurant.management.exception.ResourceNotFoundException;
import com.restaurant.management.repository.RestaurantTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestaurantTableServiceImpl implements RestaurantTableService {

    private final RestaurantTableRepository tableRepository;

    @Override
    @Transactional
    public TableResponse createTable(TableRequest request) {
        if (tableRepository.findByTableNumberAndDeletedAtIsNull(request.getTableNumber()).isPresent()) {
            throw new BadRequestException("Table number already exists");
        }

        RestaurantTable table = RestaurantTable.builder()
                .tableNumber(request.getTableNumber())
                .capacity(request.getCapacity())
                .location(request.getLocation())
                .build();

        RestaurantTable savedTable = tableRepository.save(table);
        return mapToResponse(savedTable);
    }

    @Override
    @Transactional
    public TableResponse updateTable(Long id, TableRequest request) {
        RestaurantTable table = tableRepository.findById(id)
                .filter(t -> t.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("Table not found"));

        if (!table.getTableNumber().equals(request.getTableNumber()) &&
                tableRepository.findByTableNumberAndDeletedAtIsNull(request.getTableNumber()).isPresent()) {
            throw new BadRequestException("Table number already exists");
        }

        table.setTableNumber(request.getTableNumber());
        table.setCapacity(request.getCapacity());
        table.setLocation(request.getLocation());

        return mapToResponse(tableRepository.save(table));
    }

    @Override
    @Transactional
    public TableResponse updateTableStatus(Long id, UpdateTableStatusRequest request) {
        RestaurantTable table = tableRepository.findById(id)
                .filter(t -> t.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("Table not found"));

        table.setStatus(request.getStatus());

        return mapToResponse(tableRepository.save(table));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TableResponse> getAllTables() {
        return tableRepository.findAllByDeletedAtIsNull().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TableResponse getTableById(Long id) {
        RestaurantTable table = tableRepository.findById(id)
                .filter(t -> t.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("Table not found"));
        return mapToResponse(table);
    }

    private TableResponse mapToResponse(RestaurantTable table) {
        return TableResponse.builder()
                .id(table.getId())
                .tableNumber(table.getTableNumber())
                .capacity(table.getCapacity())
                .status(table.getStatus())
                .location(table.getLocation())
                .createdAt(table.getCreatedAt())
                .updatedAt(table.getUpdatedAt())
                .build();
    }
}
