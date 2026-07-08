package com.auth.server.domain.persistence.models.token;

import com.auth.server.domain.response.exceptions.authorizationExceptions.InvalidTokenException;
import com.auth.server.domain.response.exceptions.authorizationExceptions.TokenExpiredException;

import lombok.Getter;

@Getter
public class Token {

  private final TokenID id;
  private final String token;
  private final String userId;
  private final String tokenUse;
  private final long expirationTime;
  private final long createdAt;
  private long updatedAt;
  private long lastAccessedAt;
  private Long usedAt;

  public Token(TokenID id, String token, String userId, String tokenUse, long expirationTime, long createdAt,
      long updatedAt, long lastAccessedAt, Long usedAt) {
    this.id = id;
    this.token = token;
    this.userId = userId;
    this.tokenUse = tokenUse;
    this.expirationTime = expirationTime;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.lastAccessedAt = lastAccessedAt;
    this.usedAt = usedAt;
  }

  public boolean isExpired() {
    return System.currentTimeMillis() > expirationTime;
  }

  public boolean isUsed() {
    return usedAt != null;
  }

  public void validateNotExpired() {
    if (isExpired()) {
      throw new TokenExpiredException("Token has expired");
    }
  }

  public void validateCanBeConsumed() {
    validateNotExpired();
    if (isUsed()) {
      throw new InvalidTokenException("Token has already been used or revoked");
    }
  }

  public void updateLastAccessedAt() {
    long now = System.currentTimeMillis();
    this.lastAccessedAt = now;
    this.updatedAt = now;
  }

  public void markAsUsed() {
    long now = System.currentTimeMillis();
    this.usedAt = now;
    this.updatedAt = now;
    this.lastAccessedAt = now;
  }

  public long getTimeUntilExpiration() {
    return expirationTime - System.currentTimeMillis();
  }

}
