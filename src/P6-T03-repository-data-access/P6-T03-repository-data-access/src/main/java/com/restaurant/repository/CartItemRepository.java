package com.restaurant.repository;

import com.restaurant.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByCart_Id(Long cartId);

    Optional<CartItem> findByCart_IdAndFoodItem_Id(Long cartId, Long foodItemId);

    void deleteByCart_IdAndFoodItem_Id(Long cartId, Long foodItemId);

    void deleteByCart_Id(Long cartId);
}
