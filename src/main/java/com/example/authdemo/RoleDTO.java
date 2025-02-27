package com.example.authdemo;

import java.io.Serializable;
import java.util.UUID;

public record RoleDTO(
        UUID id,
        String name
) implements Serializable {}
