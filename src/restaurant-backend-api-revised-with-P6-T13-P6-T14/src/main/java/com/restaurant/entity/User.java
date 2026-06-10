package com.restaurant.entity;

import com.restaurant.model.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity @Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id") private Long userId;
    @Column(name = "full_name", nullable = false, length = 100) private String fullName;
    @Column(name = "email", nullable = false, unique = true, length = 255) private String email;
    @Column(name = "password_hash", nullable = false, length = 255) private String passwordHash;
    @Column(name = "phone", length = 20) private String phone;
    @Column(name = "address", length = 255) private String address;
    @Enumerated(EnumType.STRING) @Column(name = "status", nullable = false, length = 20) private UserStatus status;
    @Column(name = "created_at", nullable = false) private LocalDateTime createdAt;
    @Column(name = "updated_at") private LocalDateTime updatedAt;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    @Builder.Default private Set<Role> roles = new HashSet<>();
    @PrePersist void prePersist(){ if(status==null) status=UserStatus.ACTIVE; if(createdAt==null) createdAt=LocalDateTime.now(); }
    @PreUpdate void preUpdate(){ updatedAt=LocalDateTime.now(); }
}
