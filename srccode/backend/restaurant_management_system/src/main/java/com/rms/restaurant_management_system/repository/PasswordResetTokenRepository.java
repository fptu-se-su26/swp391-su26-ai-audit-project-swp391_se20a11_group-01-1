package com.rms.restaurant_management_system.repository;

import com.rms.restaurant_management_system.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findFirstByEmailAndUsedAtIsNullOrderByCreatedAtDesc(String email);

    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query("update PasswordResetToken t set t.failedAttempts = t.failedAttempts + 1 "
            + "where t.id = :id and t.usedAt is null and t.failedAttempts < :maxAttempts")
    int recordFailedAttempt(@Param("id") Long id, @Param("maxAttempts") int maxAttempts);

    @Modifying
    @Query("update PasswordResetToken t set t.usedAt = :usedAt "
            + "where t.id = :id and t.usedAt is null and t.expiresAt >= :usedAt "
            + "and t.failedAttempts < :maxAttempts and t.tokenHash = :tokenHash")
    int consumeIfValid(@Param("id") Long id, @Param("tokenHash") String tokenHash,
                       @Param("usedAt") LocalDateTime usedAt, @Param("maxAttempts") int maxAttempts);
}
