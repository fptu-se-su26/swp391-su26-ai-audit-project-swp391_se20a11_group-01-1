package com.rms.restaurant_management_system.error;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.OffsetDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ApiErrorResponse(
        OffsetDateTime timestamp,
        int status,
        String code,
        String message,
        String path,
        String correlationId,
        List<FieldViolation> fieldErrors) {
}
