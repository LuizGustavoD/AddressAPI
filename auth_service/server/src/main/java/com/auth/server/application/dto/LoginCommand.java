package com.auth.server.application.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginCommand(
    @NotBlank(message = "Username cannot be blank")
    String username,

    @NotBlank(message = "Password cannot be blank")
    String password
) {}
