package com.rms.restaurant_management_system.repository;

import com.rms.restaurant_management_system.entity.VoucherUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoucherUsageRepository extends JpaRepository<VoucherUsage, Long> {
    int countByVoucherIdAndUserUserIdAndStatusNot(Long voucherId, Long userId, com.rms.restaurant_management_system.enums.VoucherUsageStatus status);
    
    long countByVoucherId(Long voucherId);
    
    java.util.Optional<VoucherUsage> findByVoucherCodeAndOrderOrderId(String code, Long orderId);
    
    java.util.Optional<VoucherUsage> findByVoucherCodeAndReservationReservationId(String code, Long reservationId);

    boolean existsByOrderOrderIdAndStatus(Long orderId, com.rms.restaurant_management_system.enums.VoucherUsageStatus status);

    boolean existsByReservationReservationIdAndStatus(Long reservationId, com.rms.restaurant_management_system.enums.VoucherUsageStatus status);
}
