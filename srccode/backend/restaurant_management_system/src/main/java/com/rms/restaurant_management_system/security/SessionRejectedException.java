package com.rms.restaurant_management_system.security;

public class SessionRejectedException extends RuntimeException {
    public SessionRejectedException(String message) {
        super(message);
    }
}
