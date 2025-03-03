package com.example.authdemo.model;

import lombok.Builder;

@Builder
public record RefreshTokenResponse(
        String accessToken,
        String refreshToken
) {}
