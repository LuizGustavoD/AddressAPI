package com.mail.service.domain.persistence.entities;

import java.time.Instant;
import java.util.UUID;

public class Access {

  private UUID id;
  private String userId;
  private String tokenHash;
  private String callerAddress;
  private Instant createdAt;

  public Access(UUID id, String userId, String tokenHash, String callerAddress, Instant createdAt) {
    this.id = id;
    this.userId = userId;
    this.tokenHash = tokenHash;
    this.callerAddress = callerAddress;
    this.createdAt = createdAt;
  }

  public static Access create(String userId, String tokenHash, String callerAddress) {
    return new Access(null, userId, tokenHash, callerAddress, Instant.now());
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


  

