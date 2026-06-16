package com.restaurant.management.security;

import com.restaurant.management.entity.auth.User;
import com.restaurant.management.entity.auth.UserRole;
import com.restaurant.management.repository.UserRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        List<UserRole> userRoles = entityManager.createQuery(
                "SELECT ur FROM UserRole ur JOIN FETCH ur.role WHERE ur.user.id = :userId", UserRole.class)
                .setParameter("userId", user.getId())
                .getResultList();

        return CustomUserDetails.build(user, userRoles);
    }
}
