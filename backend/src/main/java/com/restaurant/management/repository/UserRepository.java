package com.restaurant.management.repository;

import com.restaurant.management.entity.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN UserRole ur ON u.id = ur.user.id LEFT JOIN Role r ON ur.role.id = r.id WHERE " +
           "(:keyword IS NULL OR LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:isActive IS NULL OR u.isActive = :isActive) AND " +
           "(:role IS NULL OR r.name = :role)")
    Page<User> searchUsers(@Param("keyword") String keyword, @Param("isActive") Boolean isActive, @Param("role") String role, Pageable pageable);

    @Query("SELECT COUNT(u) FROM User u JOIN UserRole ur ON u.id = ur.user.id JOIN Role r ON ur.role.id = r.id WHERE r.name = 'ROLE_ADMIN' AND u.isActive = true")
    long countActiveAdmins();
}
