package com.rms.restaurant_management_system.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ApiErrorWriter {
    private final ObjectMapper objectMapper;

    public ApiErrorResponse response(HttpServletRequest request, HttpStatus status, ErrorCode code,
                                     String message, List<FieldViolation> fieldErrors) {
        return new ApiErrorResponse(OffsetDateTime.now(), status.value(), code.name(), message,
                request.getRequestURI(), CorrelationIdFilter.from(request), fieldErrors);
    }

    public void write(HttpServletRequest request, HttpServletResponse response, HttpStatus status,
                      ErrorCode code, String message) throws IOException {
        response.setStatus(status.value());
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setHeader(CorrelationIdFilter.HEADER, CorrelationIdFilter.from(request));
        objectMapper.writeValue(response.getOutputStream(), response(request, status, code, message, List.of()));
    }
}
