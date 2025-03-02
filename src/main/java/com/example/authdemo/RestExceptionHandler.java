package com.example.authdemo;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler({BadRequestException.class, HttpMessageNotReadableException.class, NullPointerException.class})
    public ResponseEntity<APIResponse<Map<String, Object>>> handleBadRequest(Exception exception, HttpServletRequest request) {
        log.error("Bad request: {}", exception.getMessage(), exception);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage(), "BAD_REQUEST_001", request);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<APIResponse<Map<String, Object>>> handleConflict(ConflictException exception, HttpServletRequest request) {
        log.error("Conflict: {}", exception.getMessage(), exception);
        return buildErrorResponse(HttpStatus.CONFLICT, exception.getMessage(), "CONFLICT_001", request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<APIResponse<Map<String, Object>>> handleDataIntegrityViolation(DataIntegrityViolationException exception, HttpServletRequest request) {
        log.error("Data integrity violation: {}", exception.getMessage());
        return buildErrorResponse(HttpStatus.CONFLICT, "Data integrity violation. Please ensure that the data you are submitting is unique.", "DATA_INTEGRITY_001", request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIResponse<Map<String, Object>>> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, HttpServletRequest request) {
        BindingResult bindingResult = exception.getBindingResult();
        String defaultMessage = "Validation failed";
        if (!bindingResult.getAllErrors().isEmpty()) {
            ObjectError firstError = bindingResult.getAllErrors().get(0);
            defaultMessage = firstError.getDefaultMessage();
        }
        log.error("Validation error: {}", defaultMessage);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, defaultMessage, "VALIDATION_001", request);
    }

    @ExceptionHandler({EntityNotFoundException.class, NotFoundException.class, UsernameNotFoundException.class})
    public ResponseEntity<APIResponse<Map<String, Object>>> handleNotFound(Exception exception, HttpServletRequest request) {
        log.error("Not found: {}", exception.getMessage(), exception);
        return buildErrorResponse(HttpStatus.NOT_FOUND, exception.getMessage(), "NOT_FOUND_001", request);
    }

    @ExceptionHandler({InvalidBearerTokenException.class, UnauthorizedException.class, BadCredentialsException.class, JwtException.class})
    public ResponseEntity<APIResponse<Map<String, Object>>> handleUnauthorized(Exception exception, HttpServletRequest request) {
        log.error("Unauthorized: {}", exception.getMessage());
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, exception.getMessage(), "UNAUTHORIZED_001", request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIResponse<Map<String, Object>>> handleGenericException(Exception exception, HttpServletRequest request) {
        log.error("Internal server error: {}", exception.getMessage(), exception);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.", "INTERNAL_SERVER_ERROR_001", request);
    }

    private ResponseEntity<APIResponse<Map<String, Object>>> buildErrorResponse(HttpStatus status, String message, String errorCode, HttpServletRequest request) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("errorCode", errorCode); // Adding error code
        APIResponse<Map<String, Object>> apiResponse = APIResponse.<Map<String, Object>>builder()
                .status(status)
                .statusCode(status.value())
                .message(message)
                .data(errorDetails) // Adding error code to data
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(status).body(apiResponse);
    }

}
