package com.mail.service.domain.persistence.entities;

import java.time.Instant;
import java.util.UUID;

public class MailSend {

  private UUID id;
  private String userId;
  private String to;
  private String subject;
  private String body;
  private String status;
  private String failureReason;
  private UUID accessId;
  private Instant createdAt;

  public MailSend(
      UUID id,
      String userId,
      String to,
      String subject,
      String body,
      String status,
      String failureReason,
      UUID accessId,
      Instant createdAt) {
    this.id = id;
    this.userId = userId;
    this.to = to;
    this.subject = subject;
    this.body = body;
    this.status = status;
    this.failureReason = failureReason;
    this.accessId = accessId;
    this.createdAt = createdAt;
  }

  public static MailSend success(String userId, String to, String subject, String body, Access access) {
    return new MailSend(
        null,
        userId,
        to,
        subject,
        body,
        "SENT",
        null,
        access.getId(),
        Instant.now());
  }

  public static MailSend failure(String userId, String to, String subject, String body, String failureReason, Access access) {
    return new MailSend(
        null,
        userId,
        to,
        subject,
        body,
        "FAILED",
        failureReason,
        access.getId(),
        Instant.now());
  }

  public UUID getId() {
    return id;
  }

  public String getUserId() {
    return userId;
  }

  public String getTo() {
    return to;
  }

  public String getSubject() {
    return subject;
  }

  public String getBody() {
    return body;
  }

  public String getStatus() {
    return status;
  }

  public String getFailureReason() {
    return failureReason;
  }

  public UUID getAccessId() {
    return accessId;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}
