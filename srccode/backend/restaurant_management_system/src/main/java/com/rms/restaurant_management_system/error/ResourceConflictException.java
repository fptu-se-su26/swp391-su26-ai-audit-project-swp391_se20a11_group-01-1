package com.rms.restaurant_management_system.error;

import org.springframework.http.HttpStatus;

public class ResourceConflictException extends ApiException {
    public ResourceConflictException(String message) {
        this(ErrorCode.RESOURCE_CONFLICT, message);
    }

    public ResourceConflictException(ErrorCode code, String message) {
        super(HttpStatus.CONFLICT, code, message);
    }
}
