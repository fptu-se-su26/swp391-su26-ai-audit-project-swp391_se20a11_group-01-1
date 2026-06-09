package com.restaurant.security;

import com.restaurant.entity.User;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
public class SecurityUser implements UserDetails {

    private final User user;

    public SecurityUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return user.getId();
    }

    public String getFullName() {
        return user.getFullName();
    }

    public String getEmail() {
        return user.getEmail();
    }

    public String getRoleName() {
        if (user.getRole() == null || user.getRole().getRoleName() == null) {
            return "CUSTOMER";
        }
        return normalizeRoleName(user.getRole().getRoleName());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + getRoleName()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !"LOCKED".equalsIgnoreCase(user.getStatus());
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return !"INACTIVE".equalsIgnoreCase(user.getStatus());
    }

    private String normalizeRoleName(String rawRole) {
        return rawRole.trim()
                .toUpperCase()
                .replace("ROLE_", "");
    }
}
