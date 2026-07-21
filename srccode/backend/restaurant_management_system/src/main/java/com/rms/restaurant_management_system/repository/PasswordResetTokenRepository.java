package com.rms.restaurant_management_system.repository;

import com.rms.restaurant_management_system.entity.PasswordResetToken;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<PasswordResetToken> findFirstByEmailAndUsedAtIsNullOrderByCreatedAtDesc(String email);
}
