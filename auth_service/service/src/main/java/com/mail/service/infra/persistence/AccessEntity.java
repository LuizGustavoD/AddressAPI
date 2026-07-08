package com.mail.service.infra.persistence;

import java.time.Instant;
import java.util.UUID;

import com.mail.service.domain.persistence.entities.Access;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "audit_access")
public class AccessEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false, updatable = false)
  private UUID id;

  @Column(name = "user_id", nullable = false, length = 64)
  private String userId;

  @Column(name = "token_hash", nullable = false, length = 64)
  private String tokenHash;

  @Column(name = "caller_address", nullable = false, length = 128)
  private String callerAddress;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  protected AccessEntity() {
  }

  public AccessEntity(UUID id, String userId, String tokenHash, String callerAddress, Instant createdAt) {
    this.id = id;
    this.userId = userId;
    this.tokenHash = tokenHash;
    this.callerAddress = callerAddress;
    this.createdAt = createdAt;
  }

  public static AccessEntity fromDomain(Access access) {
    return new AccessEntity(
        access.getId(),
        access.getUserId(),
        access.getTokenHash(),
        access.getCallerAddress(),
        access.getCreatedAt());
  }

  public Access toDomain() {
    return new Access(id, userId, tokenHash, callerAddress, createdAt);
  }

  public UUID getId() {
    return id;
  }

  public String getUserId() {
    return userId;
  }

  public String getTokenHash() {
    return tokenHash;
  }

  public String getCallerAddress() {
    return callerAddress;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}
