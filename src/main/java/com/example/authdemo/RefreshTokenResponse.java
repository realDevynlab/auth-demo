package com.example.authdemo;

import lombok.Builder;

@Builder
public record RefreshTokenResponse(
        String accessToken,
        String refreshToken
) {}
