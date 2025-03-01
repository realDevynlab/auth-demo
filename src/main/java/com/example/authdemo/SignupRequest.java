package com.example.authdemo;

public record SignupRequest(
        String username,
        String email,
        String password
) {}
