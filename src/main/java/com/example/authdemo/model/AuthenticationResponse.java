package com.example.authdemo.model;

import com.example.authdemo.dto.UserDTO;
import lombok.Builder;

@Builder
public record AuthenticationResponse(
        UserDTO user,
        String accessToken,
        String refreshToken
) {}
