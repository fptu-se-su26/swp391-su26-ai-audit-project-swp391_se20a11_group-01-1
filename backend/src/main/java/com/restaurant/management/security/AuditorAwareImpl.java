package com.restaurant.management.security;

import org.springframework.data.domain.AuditorAware;
import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<Long> {
    @Override
    public Optional<Long> getCurrentAuditor() {
        // Placeholder for system user ID or current authenticated user ID
        return Optional.of(0L); // 0L represents SYSTEM
    }
}
