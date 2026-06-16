package com.restaurant.management.repository;

import com.restaurant.management.entity.order.RestaurantOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<RestaurantOrder, Long> {
    List<RestaurantOrder> findByCustomerId(Long customerId);
    Optional<RestaurantOrder> findByIdAndCustomerId(Long id, Long customerId);
}
