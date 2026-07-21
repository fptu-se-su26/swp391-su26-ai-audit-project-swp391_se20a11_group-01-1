package com.rms.restaurant_management_system.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(length = 150)
    private String fullName;

    @Column(length = 30)
    private String phone;

    @Column(length = 500)
    private String avatarUrl;

    @Column(nullable = false, length = 255)
    private String passwordHash;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isActive = true;

    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @Column(nullable = false)
    private Integer tokenVersion = 0;

    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
