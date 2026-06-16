package com.restaurant.management.repository;

import com.restaurant.management.entity.auth.UserRole;
import com.restaurant.management.entity.auth.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {
    List<UserRole> findByUserId(Long userId);
}
