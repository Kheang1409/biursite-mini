package com.biursite.config;

import com.biursite.application.shared.exception.BadRequestException;
import com.biursite.application.shared.exception.ForbiddenException;
import com.biursite.application.shared.exception.ResourceNotFoundException;
import com.biursite.application.shared.exception.UnauthorizedException;
import com.biursite.infrastructure.web.dto.ApiResponse;
import com.biursite.domain.shared.exception.DomainForbiddenException;
import com.biursite.domain.shared.exception.ConcurrencyConflictException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import jakarta.persistence.OptimisticLockException;
import java.util.Objects;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice(annotations = org.springframework.web.bind.annotation.RestController.class)
public class ApiExceptionHandler {

    private ResponseEntity<ApiResponse<Void>> errorResponse(int status, String error, String message, HttpServletRequest request) {
        ApiResponse<Void> body = ApiResponse.failure(status, error, message, request.getRequestURI());
        return ResponseEntity.status(status).contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON)).body(body);
    }

    private ResponseEntity<ApiResponse<Void>> errorResponse(int status, String error, String message, HttpServletRequest request, Map<String, Object> meta) {
        ApiResponse<Void> body = ApiResponse.failure(status, error, message, request.getRequestURI(), meta);
        return ResponseEntity.status(status).contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON)).body(body);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return errorResponse(HttpStatus.NOT_FOUND.value(), "Not Found", ex.getMessage(), request);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnauthorized(UnauthorizedException ex, HttpServletRequest request) {
        return errorResponse(HttpStatus.UNAUTHORIZED.value(), "Unauthorized", ex.getMessage(), request);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiResponse<Void>> handleForbidden(ForbiddenException ex, HttpServletRequest request) {
        return errorResponse(HttpStatus.FORBIDDEN.value(), "Forbidden", ex.getMessage(), request);
    }

    @ExceptionHandler(DomainForbiddenException.class)
    public ResponseEntity<ApiResponse<Void>> handleDomainForbidden(DomainForbiddenException ex, HttpServletRequest request) {
        return errorResponse(HttpStatus.FORBIDDEN.value(), "Forbidden", ex.getMessage(), request);
    }

    @ExceptionHandler(ConcurrencyConflictException.class)
    public ResponseEntity<ApiResponse<Void>> handleConcurrencyConflict(ConcurrencyConflictException ex, HttpServletRequest request) {
        return errorResponse(HttpStatus.CONFLICT.value(), "Conflict", ex.getMessage(), request);
    }

    @ExceptionHandler({ObjectOptimisticLockingFailureException.class, OptimisticLockException.class})
    public ResponseEntity<ApiResponse<Void>> handleOptimisticLocking(Exception ex, HttpServletRequest request) {
        return errorResponse(HttpStatus.CONFLICT.value(), "Conflict", "Concurrent update detected", request);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(BadRequestException ex, HttpServletRequest request) {
        return errorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request", ex.getMessage(), request);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(NoHandlerFoundException ex, HttpServletRequest request) {
        return errorResponse(HttpStatus.NOT_FOUND.value(), "Not Found", "Resource not found", request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        return errorResponse(HttpStatus.FORBIDDEN.value(), "Forbidden", "Access denied", request);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthException(AuthenticationException ex, HttpServletRequest request) {
        return errorResponse(HttpStatus.UNAUTHORIZED.value(), "Unauthorized", ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
        Map<String, Object> meta = new LinkedHashMap<>();
        meta.put("errors", errors);
        return errorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request", "Validation failed", request, meta);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleOther(Exception ex, HttpServletRequest request) {
        return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", "Internal server error", request);
    }
}
