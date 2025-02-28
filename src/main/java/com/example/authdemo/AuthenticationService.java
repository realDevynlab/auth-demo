package com.example.authdemo;

public interface AuthenticationService {

    AuthenticationResponse login(LoginDTO loginDTO);

    RefreshTokenResponse refreshToken(String refreshToken);

}
