package com.example.authdemo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserDTO(
        UUID id,
        String username,
        String email,
        String firstName,
        String lastName,
        List<RoleDTO> roles
) {}
