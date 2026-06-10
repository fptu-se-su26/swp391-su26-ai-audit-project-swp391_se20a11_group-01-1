package com.restaurant.repository;
import com.restaurant.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCart_CartIdAndFood_FoodId(Long cartId, Long foodId);
    List<CartItem> findByCart_CartId(Long cartId);
}
