package com.restaurant.repository;

import com.restaurant.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

    Optional<Coupon> findByCode(String code);

    boolean existsByCode(String code);

    List<Coupon> findByStatus(String status);

    @Query("""
            SELECT c FROM Coupon c
            WHERE c.code = :code
              AND c.status = 'ACTIVE'
              AND (c.startDate IS NULL OR c.startDate <= :now)
              AND (c.endDate IS NULL OR c.endDate >= :now)
            """)
    Optional<Coupon> findActiveCouponByCode(@Param("code") String code,
                                            @Param("now") LocalDateTime now);
}
