package com.rms.restaurant_management_system.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "security_audit_logs")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class SecurityAuditLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long auditId;
    private Long actorUserId;
    private Long targetUserId;
    @Column(nullable = false, length = 80)
    private String action;
    @Column(length = 500)
    private String oldValue;
    @Column(length = 500)
    private String newValue;
    @Column(length = 45)
    private String ipAddress;
    @Column(nullable = false)
    private LocalDateTime createdAt;
}
