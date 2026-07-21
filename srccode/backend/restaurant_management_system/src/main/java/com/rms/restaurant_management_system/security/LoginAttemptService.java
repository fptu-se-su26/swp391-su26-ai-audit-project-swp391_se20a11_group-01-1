package com.rms.restaurant_management_system.security;

import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;
import com.rms.restaurant_management_system.error.RateLimitException;

@Service
public class LoginAttemptService {
    private static final int MAX_FAILURES = 5;
    private static final long WINDOW_MINUTES = 15;
    private final ConcurrentHashMap<String, Attempt> attempts = new ConcurrentHashMap<>();

    public void assertAllowed(String key) {
        Attempt attempt = attempts.get(key);
        if (attempt == null) return;
        if (attempt.firstFailure().isBefore(Instant.now().minus(WINDOW_MINUTES, ChronoUnit.MINUTES))) {
            attempts.remove(key, attempt);
            return;
        }
        if (attempt.failures() >= MAX_FAILURES) {
            throw new RateLimitException("Too many login attempts. Please try again later");
        }
    }

    public void failed(String key) {
        attempts.compute(key, (ignored, current) -> current == null
                || current.firstFailure().isBefore(Instant.now().minus(WINDOW_MINUTES, ChronoUnit.MINUTES))
                ? new Attempt(1, Instant.now()) : new Attempt(current.failures() + 1, current.firstFailure()));
    }

    public void succeeded(String key) {
        attempts.remove(key);
    }

    private record Attempt(int failures, Instant firstFailure) {}
}
