package com.example.authdemo.controller;

import com.example.authdemo.model.*;
import com.example.authdemo.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("authentication")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("login")
    public ResponseEntity<APIResponse<Map<String, AuthenticationResponse>>> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        AuthenticationResponse authenticationResponse = authenticationService.login(loginRequest);
        APIResponse<Map<String, AuthenticationResponse>> apiResponse = APIResponse.<Map<String, AuthenticationResponse>>builder()
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .message("Login successful")
                .data(Map.of("response", authenticationResponse))
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok().body(apiResponse);
    }

    @PostMapping("refresh-token")
    public ResponseEntity<APIResponse<Map<String, RefreshTokenResponse>>> refreshToken(@RequestBody RefreshTokenRequest tokenRequest, HttpServletRequest servletRequest) {
        RefreshTokenResponse refreshTokenResponse = authenticationService.refreshToken(tokenRequest.refreshToken());
        APIResponse<Map<String, RefreshTokenResponse>> apiResponse = APIResponse.<Map<String, RefreshTokenResponse>>builder()
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .message("Token refresh successful")
                .data(Map.of("response", refreshTokenResponse))
                .path(servletRequest.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok().body(apiResponse);
    }

}
