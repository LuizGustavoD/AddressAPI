package com.auth.server.application.dto;

public record AuthResponse(
    String accessToken,
    String refreshToken,
    Long expiresIn
) {}
