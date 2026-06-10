package com.restaurant.repository;
import com.restaurant.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByCustomer_UserIdAndCartStatus(Long userId, com.restaurant.model.CartStatus status);
}
