package com.restaurant.repository;

import com.restaurant.entity.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {

    Optional<RestaurantTable> findByTableNumber(String tableNumber);

    boolean existsByTableNumber(String tableNumber);

    List<RestaurantTable> findByTableStatus(String tableStatus);

    List<RestaurantTable> findByCapacityGreaterThanEqual(Integer capacity);
}
