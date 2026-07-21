package com.rms.restaurant_management_system.error;

import org.springframework.http.HttpStatus;

public class RateLimitException extends ApiException {
    public RateLimitException(String message) {
        super(HttpStatus.TOO_MANY_REQUESTS, ErrorCode.RATE_LIMIT_EXCEEDED, message);
    }
}
