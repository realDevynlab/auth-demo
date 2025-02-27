package com.example.authdemo;

import lombok.Builder;

@Builder
public record AuthenticationResponse(
        UserDTO user,
        String accessToken,
        String refreshToken
) {}
