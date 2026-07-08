package com.auth.server.application.dto;

public record MailRequest(
    String userId,
    String activationToken,
    String to,
    String subject,
    String body
) {
}
