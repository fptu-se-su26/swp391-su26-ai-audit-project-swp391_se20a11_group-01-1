package com.rms.restaurant_management_system.repository;

import com.rms.restaurant_management_system.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<RefreshToken> findLockedByTokenHash(String tokenHash);
    @Modifying
    @Query("update RefreshToken t set t.revokedAt = :now where t.user.userId = :userId and t.revokedAt is null")
    int revokeAllByUserId(Long userId, LocalDateTime now);
    @Modifying
    @Query("update RefreshToken t set t.revokedAt = :now where t.familyId = :familyId and t.revokedAt is null")
    int revokeFamily(String familyId, LocalDateTime now);
}
