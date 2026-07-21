package com.rms.restaurant_management_system.error;

import org.springframework.http.HttpStatus;

public class ExternalServiceException extends ApiException {
    public ExternalServiceException(String message, Throwable cause) {
        super(HttpStatus.SERVICE_UNAVAILABLE, ErrorCode.EXTERNAL_SERVICE_UNAVAILABLE, message, cause);
    }
}
