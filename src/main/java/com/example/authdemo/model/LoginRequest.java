package com.example.authdemo.model;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
		@NotBlank(message = "Username or Email should not be empty")
		String usernameOrEmail,

		@NotBlank(message = "Password should not be empty")
		String password
) {}
