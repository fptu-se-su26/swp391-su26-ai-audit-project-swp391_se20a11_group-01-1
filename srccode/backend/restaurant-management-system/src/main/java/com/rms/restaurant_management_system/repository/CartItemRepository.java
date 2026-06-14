package com.rms.restaurant_management_system.repository;

import com.rms.restaurant_management_system.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartIdAndFoodId(Long cartId, Long foodId);
}
