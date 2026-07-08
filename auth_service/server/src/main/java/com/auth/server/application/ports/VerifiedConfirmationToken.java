package com.auth.server.application.ports;

public record VerifiedConfirmationToken(
    String userId,
    String email
) {
}