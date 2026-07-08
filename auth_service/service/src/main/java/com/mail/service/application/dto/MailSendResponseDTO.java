package com.mail.service.application.dto;

import java.time.Instant;
import java.util.UUID;

public record MailSendResponseDTO(
    UUID accessAuditId,
    UUID mailAuditId,
    String userId,
    String status,
    Instant processedAt
) {
}
