package com.rms.restaurant_management_system.error;

import org.springframework.http.HttpStatus;

public class BusinessRuleException extends ApiException {
    public BusinessRuleException(String message) {
        this(ErrorCode.BUSINESS_RULE_VIOLATION, message);
    }

    public BusinessRuleException(ErrorCode code, String message) {
        super(HttpStatus.BAD_REQUEST, code, message);
    }
}
