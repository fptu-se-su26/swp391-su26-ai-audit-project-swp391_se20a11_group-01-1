package com.rms.restaurant_management_system.repository;

import com.rms.restaurant_management_system.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    Optional<Voucher> findByCode(String code);
    
    boolean existsByCode(String code);
    
    List<Voucher> findByActiveTrue();
}
