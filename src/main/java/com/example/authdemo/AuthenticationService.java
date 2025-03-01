package com.example.authdemo;

public interface AuthenticationService {

    AuthenticationResponse login(LoginRequest loginRequest);

    RefreshTokenResponse refreshToken(String refreshToken);

}
