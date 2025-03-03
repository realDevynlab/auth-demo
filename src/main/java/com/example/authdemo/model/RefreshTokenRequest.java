package com.example.authdemo.model;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @NotBlank(message = "Refresh token should not be empty")
        String refreshToken
) {}
