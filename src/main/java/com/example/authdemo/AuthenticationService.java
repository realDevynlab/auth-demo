package com.example.authdemo;

import java.util.Optional;
import java.util.UUID;

public interface AuthenticationService {

    AuthenticationResponse login(LoginDTO loginDTO);

    RefreshToken createRefreshToken(UUID userId);

    RefreshTokenResponse refreshToken(String refreshToken);

    Optional<RefreshToken> findByToken(String token);

    RefreshToken verifyExpiration(RefreshToken token);

}
