package com.example.authdemo.dto;

import java.io.Serializable;
import java.util.UUID;

public record RoleDTO(
        UUID id,
        String name
) implements Serializable {}
