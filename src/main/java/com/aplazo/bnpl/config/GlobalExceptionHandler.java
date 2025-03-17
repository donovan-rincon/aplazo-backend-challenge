package com.aplazo.bnpl.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.aplazo.bnpl.model.dto.ErrorResponse;

import java.time.Instant;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleInternalServerError(Exception ex, WebRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("BNPL000001") // General internal server error
                .error("INTERNAL_SERVER_ERROR")
                .timestamp(Instant.now().toEpochMilli())
                .message("An unexpected error occurred: " + ex.getMessage())
                .path(request.getDescription(false))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRequestException(IllegalArgumentException ex,
            WebRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("BNPL000004") // Invalid request error code
                .error("INVALID_REQUEST")
                .timestamp(Instant.now().toEpochMilli())
                .message("Invalid request: " + ex.getMessage())
                .path(request.getDescription(false))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedRequestException(SecurityException ex,
            WebRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("BNPL000007") // Unauthorized error code
                .error("UNAUTHORIZED")
                .timestamp(Instant.now().toEpochMilli())
                .message("You are not authorized to access this resource.")
                .path(request.getDescription(false))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }
}
