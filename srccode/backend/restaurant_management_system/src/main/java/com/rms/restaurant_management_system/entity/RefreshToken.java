package com.rms.restaurant_management_system.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_tokens", indexes = {
        @Index(name = "ix_refresh_tokens_hash", columnList = "token_hash", unique = true),
        @Index(name = "ix_refresh_tokens_user", columnList = "user_id"),
        @Index(name = "ix_refresh_tokens_family", columnList = "family_id")
})
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class RefreshToken {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long refreshTokenId;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(name = "token_hash", nullable = false, length = 64, unique = true)
    private String tokenHash;
    @Column(name = "family_id", nullable = false, length = 36)
    private String familyId;
    @Column(nullable = false)
    private LocalDateTime expiresAt;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    private LocalDateTime lastUsedAt;
    private LocalDateTime revokedAt;
    @Column(length = 45)
    private String ipAddress;
    @Column(length = 500)
    private String userAgent;
}
