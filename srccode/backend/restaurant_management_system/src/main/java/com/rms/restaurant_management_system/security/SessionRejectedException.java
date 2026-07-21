package com.rms.restaurant_management_system.security;

import com.rms.restaurant_management_system.error.ApiException;
import com.rms.restaurant_management_system.error.ErrorCode;
import org.springframework.http.HttpStatus;

public class SessionRejectedException extends ApiException {
    public SessionRejectedException(String message) {
        super(HttpStatus.UNAUTHORIZED, ErrorCode.SESSION_REJECTED, message);
    }
}
