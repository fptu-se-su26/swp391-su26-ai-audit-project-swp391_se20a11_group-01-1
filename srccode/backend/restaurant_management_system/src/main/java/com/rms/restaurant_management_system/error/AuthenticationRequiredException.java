package com.rms.restaurant_management_system.error;

import org.springframework.http.HttpStatus;

public class AuthenticationRequiredException extends ApiException {
    public AuthenticationRequiredException(ErrorCode code, String message) {
        super(HttpStatus.UNAUTHORIZED, code, message);
    }
}
