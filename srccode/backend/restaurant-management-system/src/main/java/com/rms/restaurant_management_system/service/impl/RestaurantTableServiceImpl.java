package com.rms.restaurant_management_system.service.impl;

import com.rms.restaurant_management_system.dto.request.RestaurantTableRequest;
import com.rms.restaurant_management_system.dto.response.RestaurantTableResponse;
import com.rms.restaurant_management_system.entity.RestaurantTable;
import com.rms.restaurant_management_system.enums.TableStatus;
import com.rms.restaurant_management_system.exception.BadRequestException;
import com.rms.restaurant_management_system.exception.ResourceNotFoundException;
import com.rms.restaurant_management_system.repository.RestaurantTableRepository;
import com.rms.restaurant_management_system.service.interfaces.RestaurantTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestaurantTableServiceImpl implements RestaurantTableService {

    private final RestaurantTableRepository tableRepository;

    @Override
    public List<RestaurantTableResponse> getAllTables() {
        return tableRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RestaurantTableResponse getTableById(Long id) {
        RestaurantTable table = getTableEntity(id);
        return mapToResponse(table);
    }

    @Override
    public RestaurantTableResponse createTable(RestaurantTableRequest request) {
        if (tableRepository.existsByNumber(request.getNumber())) {
            throw new BadRequestException("Table number already exists");
        }

        RestaurantTable table = RestaurantTable.builder()
                .number(request.getNumber())
                .capacity(request.getCapacity())
                .status(request.getStatus() != null ? request.getStatus() : TableStatus.AVAILABLE)
                .build();

        RestaurantTable savedTable = tableRepository.save(table);
        return mapToResponse(savedTable);
    }

    @Override
    public RestaurantTableResponse updateTable(Long id, RestaurantTableRequest request) {
        RestaurantTable table = getTableEntity(id);

        if (tableRepository.existsByNumberAndIdNot(request.getNumber(), id)) {
            throw new BadRequestException("Table number already exists");
        }

        table.setNumber(request.getNumber());
        table.setCapacity(request.getCapacity());
        if (request.getStatus() != null) {
            table.setStatus(request.getStatus());
        }

        RestaurantTable updatedTable = tableRepository.save(table);
        return mapToResponse(updatedTable);
    }

    @Override
    public void deleteTable(Long id) {
        RestaurantTable table = getTableEntity(id);
        try {
            tableRepository.delete(table);
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Cannot delete table because it has associated reservations.");
        }
    }

    private RestaurantTable getTableEntity(Long id) {
        return tableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant Table not found with id: " + id));
    }

    private RestaurantTableResponse mapToResponse(RestaurantTable table) {
        return RestaurantTableResponse.builder()
                .id(table.getId())
                .number(table.getNumber())
                .capacity(table.getCapacity())
                .status(table.getStatus())
                .build();
    }
}
