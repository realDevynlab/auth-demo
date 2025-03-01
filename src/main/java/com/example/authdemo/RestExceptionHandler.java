package com.example.authdemo;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler({BadRequestException.class, HttpMessageNotReadableException.class, NullPointerException.class})
    public ResponseEntity<APIResponse<Map<String, Object>>> handleBadRequest(Exception exception, HttpServletRequest request) {
        APIResponse<Map<String, Object>> apiResponse = APIResponse.<Map<String, Object>>builder()
                .status(HttpStatus.BAD_REQUEST)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message(exception.getMessage())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<APIResponse> handleConflict(ConflictException exception, HttpServletRequest request) {
        APIResponse apiResponse = APIResponse.builder()
                .status(HttpStatus.CONFLICT)
                .statusCode(HttpStatus.CONFLICT.value())
                .message(exception.getMessage())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(apiResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, HttpServletRequest request) {
        BindingResult bindingResult = exception.getBindingResult();
        String defaultMessage = "Validation failed";
        if (!bindingResult.getAllErrors().isEmpty()) {
            ObjectError firstError = bindingResult.getAllErrors().get(0);
            defaultMessage = firstError.getDefaultMessage();
        }
        APIResponse apiResponse = APIResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message(defaultMessage)
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }

    @ExceptionHandler({EntityNotFoundException.class, NotFoundException.class, UsernameNotFoundException.class})
    public ResponseEntity<APIResponse> handleNotFound(Exception exception, HttpServletRequest request) {
        APIResponse apiResponse = APIResponse.builder()
                .status(HttpStatus.NOT_FOUND)
                .statusCode(HttpStatus.NOT_FOUND.value())
                .message(exception.getMessage())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
    }

    @ExceptionHandler({InvalidBearerTokenException.class, UnauthorizedException.class, BadCredentialsException.class})
    public ResponseEntity<APIResponse<Map<String, Object>>> handleUnauthorized(Exception exception, HttpServletRequest request) {
        APIResponse<Map<String, Object>> apiResponse = APIResponse.<Map<String, Object>>builder()
                .status(HttpStatus.UNAUTHORIZED)
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .message(exception.getMessage())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
    }

}
