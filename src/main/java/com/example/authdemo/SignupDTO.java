package com.example.authdemo;

public record SignupDTO(
        String username,
        String email,
        String password
) {}
