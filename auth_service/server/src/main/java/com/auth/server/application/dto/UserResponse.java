package com.auth.server.application.dto;

public record UserResponse(
    String id,
    String username,
    String email,
    boolean isActive
) {}
