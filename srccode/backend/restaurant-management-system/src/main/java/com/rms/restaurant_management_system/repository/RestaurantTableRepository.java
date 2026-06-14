package com.rms.restaurant_management_system.repository;

import com.rms.restaurant_management_system.entity.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {
    boolean existsByNumber(Integer number);
    boolean existsByNumberAndIdNot(Integer number, Long id);
}
