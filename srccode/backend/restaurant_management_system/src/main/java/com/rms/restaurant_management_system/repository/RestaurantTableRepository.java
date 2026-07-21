package com.rms.restaurant_management_system.repository;

import com.rms.restaurant_management_system.entity.RestaurantTable;
import com.rms.restaurant_management_system.enums.TableStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {

    Optional<RestaurantTable> findByTableName(String tableName);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select restaurantTable from RestaurantTable restaurantTable where restaurantTable.tableName = :tableName")
    Optional<RestaurantTable> findByTableNameForUpdate(@Param("tableName") String tableName);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select restaurantTable from RestaurantTable restaurantTable where restaurantTable.tableId = :tableId")
    Optional<RestaurantTable> findByTableIdForUpdate(@Param("tableId") Long tableId);

    boolean existsByTableName(String tableName);

    List<RestaurantTable> findByIsActiveTrueOrderByTableIdAsc();

    List<RestaurantTable> findByStatusAndIsActiveTrueOrderByTableIdAsc(TableStatus status);

    List<RestaurantTable> findByIsActiveTrueAndCapacityGreaterThanEqualOrderByCapacityAscTableIdAsc(Integer capacity);
}
