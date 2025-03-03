package com.example.authdemo.service;

import com.example.authdemo.model.AuthenticationResponse;
import com.example.authdemo.model.LoginRequest;
import com.example.authdemo.model.RefreshTokenResponse;

public interface AuthenticationService {

    AuthenticationResponse login(LoginRequest loginRequest);

    RefreshTokenResponse refreshToken(String refreshToken);

}
