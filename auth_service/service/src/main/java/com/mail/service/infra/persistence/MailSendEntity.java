package com.mail.service.infra.persistence;

import java.time.Instant;
import java.util.UUID;

import com.mail.service.domain.persistence.entities.MailSend;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "audit_mail_send")
public class MailSendEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false, updatable = false)
  private UUID id;

  @Column(name = "user_id", nullable = false, length = 64)
  private String userId;

  @Column(name = "target_email", nullable = false, length = 320)
  private String to;

  @Column(name = "subject", nullable = false, length = 255)
  private String subject;

  @Column(name = "body", nullable = false, length = 8192)
  private String body;

  @Column(name = "status", nullable = false, length = 16)
  private String status;

  @Column(name = "failure_reason", length = 1024)
  private String failureReason;

  @ManyToOne(optional = false)
  @JoinColumn(name = "access_id", nullable = false)
  private AccessEntity access;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  protected MailSendEntity() {
  }

  public MailSendEntity(
      UUID id,
      String userId,
      String to,
      String subject,
      String body,
      String status,
      String failureReason,
      AccessEntity access,
      Instant createdAt) {
    this.id = id;
    this.userId = userId;
    this.to = to;
    this.subject = subject;
    this.body = body;
    this.status = status;
    this.failureReason = failureReason;
    this.access = access;
    this.createdAt = createdAt;
  }

  public static MailSendEntity fromDomain(MailSend mailSend, AccessEntity accessEntity) {
    return new MailSendEntity(
        mailSend.getId(),
        mailSend.getUserId(),
        mailSend.getTo(),
        mailSend.getSubject(),
        mailSend.getBody(),
        mailSend.getStatus(),
        mailSend.getFailureReason(),
        accessEntity,
        mailSend.getCreatedAt());
  }

  public MailSend toDomain() {
    return new MailSend(
        id,
        userId,
        to,
        subject,
        body,
        status,
        failureReason,
        access.getId(),
        createdAt);
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

  public AccessEntity getAccess() {
    return access;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}
