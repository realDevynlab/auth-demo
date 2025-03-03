package com.example.authdemo.model;

public record SignupRequest(
        String username,
        String email,
        String password
) {}
