package com.restaurant.management.repository;

import com.restaurant.management.entity.reservation.RestaurantTable;
import com.restaurant.management.entity.reservation.TableStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {
    Optional<RestaurantTable> findByTableNumberAndDeletedAtIsNull(String tableNumber);
    List<RestaurantTable> findAllByDeletedAtIsNull();

    List<RestaurantTable> findByStatusAndDeletedAtIsNull(TableStatus status);
    List<RestaurantTable> findByDeletedAtIsNull();

    @Query("SELECT COUNT(t) FROM RestaurantTable t WHERE t.status = 'AVAILABLE' AND t.deletedAt IS NULL")
    Long countAvailableTables();
}
