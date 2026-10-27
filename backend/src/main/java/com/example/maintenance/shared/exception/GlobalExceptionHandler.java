package com.example.maintenance.shared.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        List<FieldErrorItem> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> new FieldErrorItem(err.getField(), err.getDefaultMessage()))
                .toList();

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "VALIDATION_FAILED",
                "Validation failed for request fields.",
                request.getRequestURI(),
                fieldErrors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }


    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableRequest(org.springframework.http.converter.HttpMessageNotReadableException ex, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "INVALID_REQUEST", "Request body contains invalid or malformed values.", request.getRequestURI());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(com.example.maintenance.auth.application.InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(
            com.example.maintenance.auth.application.InvalidCredentialsException ex, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "INVALID_CREDENTIALS",
                "Invalid email or password.", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(com.example.maintenance.complaint.application.InvalidStatusTransitionException.class)
    public ResponseEntity<ErrorResponse> handleInvalidStatusTransition(
            com.example.maintenance.complaint.application.InvalidStatusTransitionException ex, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(HttpStatus.CONFLICT.value(), "INVALID_STATUS_TRANSITION", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(com.example.maintenance.complaint.application.ActiveComplaintExistsException.class)
    public ResponseEntity<ErrorResponse> handleActiveComplaintExists(
            com.example.maintenance.complaint.application.ActiveComplaintExistsException ex, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(HttpStatus.CONFLICT.value(), "ACTIVE_COMPLAINT_EXISTS", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(org.springframework.security.access.AccessDeniedException ex, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(HttpStatus.FORBIDDEN.value(), "FORBIDDEN", "You do not have permission to access this resource.", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(com.example.maintenance.society.application.ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            com.example.maintenance.society.application.ResourceNotFoundException ex, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(HttpStatus.NOT_FOUND.value(), "RESOURCE_NOT_FOUND", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(com.example.maintenance.society.application.ResourceConflictException.class)
    public ResponseEntity<ErrorResponse> handleResourceConflict(
            com.example.maintenance.society.application.ResourceConflictException ex, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(HttpStatus.CONFLICT.value(), "RESOURCE_CONFLICT", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataConflict(org.springframework.dao.DataIntegrityViolationException ex, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(HttpStatus.CONFLICT.value(), "RESOURCE_CONFLICT", "The requested change conflicts with existing data.", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException ex, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "INVALID_REQUEST", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(
            Exception ex,
            HttpServletRequest request
    ) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_SERVER_ERROR",
                ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
