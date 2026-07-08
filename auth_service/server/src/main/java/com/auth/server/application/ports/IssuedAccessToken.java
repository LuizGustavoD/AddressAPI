package com.auth.server.application.ports;

public record IssuedAccessToken(
    String token,
    long expiresIn
) {
}