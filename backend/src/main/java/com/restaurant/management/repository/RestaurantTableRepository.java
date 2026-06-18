package com.restaurant.management.repository;

import com.restaurant.management.entity.reservation.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {
    Optional<RestaurantTable> findByTableNumberAndDeletedAtIsNull(String tableNumber);
    List<RestaurantTable> findAllByDeletedAtIsNull();
}
