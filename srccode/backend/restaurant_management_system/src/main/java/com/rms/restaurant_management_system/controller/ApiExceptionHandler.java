package com.rms.restaurant_management_system.controller;

import com.rms.restaurant_management_system.error.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Comparator;
import java.util.List;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ApiExceptionHandler {
    private final ApiErrorWriter errorWriter;

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorResponse> api(ApiException ex, HttpServletRequest request) {
        return response(request, ex.getStatus(), ex.getErrorCode(), ex.getMessage(), List.of());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> accessDenied(AccessDeniedException ex, HttpServletRequest request) {
        return response(request, HttpStatus.FORBIDDEN, ErrorCode.ACCESS_DENIED,
                "Bạn không có quyền thực hiện thao tác này", List.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> validation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<FieldViolation> fields = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new FieldViolation(error.getField(),
                        error.getDefaultMessage() == null ? "Giá trị không hợp lệ" : error.getDefaultMessage()))
                .sorted(Comparator.comparing(FieldViolation::field)).toList();
        return response(request, HttpStatus.BAD_REQUEST, ErrorCode.VALIDATION_ERROR,
                "Dữ liệu không hợp lệ", fields);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> constraint(ConstraintViolationException ex, HttpServletRequest request) {
        List<FieldViolation> fields = ex.getConstraintViolations().stream()
                .map(error -> new FieldViolation(error.getPropertyPath().toString(), error.getMessage()))
                .sorted(Comparator.comparing(FieldViolation::field)).toList();
        return response(request, HttpStatus.BAD_REQUEST, ErrorCode.VALIDATION_ERROR,
                "Dữ liệu không hợp lệ", fields);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> malformed(HttpMessageNotReadableException ex, HttpServletRequest request) {
        return response(request, HttpStatus.BAD_REQUEST, ErrorCode.MALFORMED_REQUEST,
                "Nội dung yêu cầu không đúng định dạng", List.of());
    }

    @ExceptionHandler({MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class,
            IllegalArgumentException.class})
    public ResponseEntity<ApiErrorResponse> badRequest(Exception ex, HttpServletRequest request) {
        return response(request, HttpStatus.BAD_REQUEST, ErrorCode.VALIDATION_ERROR,
                "Tham số yêu cầu không hợp lệ", List.of());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiErrorResponse> noResource(NoResourceFoundException ex, HttpServletRequest request) {
        return response(request, HttpStatus.NOT_FOUND, ErrorCode.RESOURCE_NOT_FOUND,
                "Không tìm thấy tài nguyên được yêu cầu", List.of());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> method(HttpRequestMethodNotSupportedException ex,
                                                    HttpServletRequest request) {
        return response(request, HttpStatus.METHOD_NOT_ALLOWED, ErrorCode.METHOD_NOT_ALLOWED,
                "Phương thức HTTP không được hỗ trợ", List.of());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> integrity(DataIntegrityViolationException ex,
                                                       HttpServletRequest request) {
        log.warn("Database constraint rejected request correlationId={}", CorrelationIdFilter.from(request));
        return response(request, HttpStatus.CONFLICT, ErrorCode.RESOURCE_CONFLICT,
                "Dữ liệu bị trùng hoặc xung đột với trạng thái hiện tại", List.of());
    }

    @ExceptionHandler({OptimisticLockingFailureException.class, PessimisticLockingFailureException.class})
    public ResponseEntity<ApiErrorResponse> concurrent(RuntimeException ex, HttpServletRequest request) {
        return response(request, HttpStatus.CONFLICT, ErrorCode.CONCURRENT_UPDATE,
                "Dữ liệu vừa được thay đổi bởi yêu cầu khác. Vui lòng thử lại", List.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> unexpected(Exception ex, HttpServletRequest request) {
        String correlationId = CorrelationIdFilter.from(request);
        log.error("Unhandled API error correlationId={} method={} path={}", correlationId,
                request.getMethod(), request.getRequestURI(), ex);
        return response(request, HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_ERROR,
                "Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau", List.of());
    }

    private ResponseEntity<ApiErrorResponse> response(HttpServletRequest request, HttpStatus status, ErrorCode code,
                                                       String message, List<FieldViolation> fields) {
        return ResponseEntity.status(status).body(errorWriter.response(request, status, code, message, fields));
    }
}
