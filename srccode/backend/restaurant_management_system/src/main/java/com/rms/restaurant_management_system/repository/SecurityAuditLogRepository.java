package com.rms.restaurant_management_system.repository;

import com.rms.restaurant_management_system.entity.SecurityAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecurityAuditLogRepository extends JpaRepository<SecurityAuditLog, Long> {
}
