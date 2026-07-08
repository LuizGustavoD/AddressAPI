package com.auth.server.infrastructure.persistence.tokens;

import com.auth.server.domain.persistence.models.token.Token;
import com.auth.server.domain.persistence.models.token.TokenID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tokens_auth_table")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenEntity {

  @Id
  @Column(name = "token_id", nullable = false, updatable = false, length = 36)
  private String tokenId;

  @Column(name = "token", nullable = false, unique = true, length = 2048)
  private String token;

  @Column(name = "user_id", nullable = false, length = 64)
  private String userId;

  @Column(name = "token_use", nullable = false, length = 32)
  private String tokenUse;

  @Column(name = "expiration_time", nullable = false)
  private long expirationTime;

  @Column(name = "created_at", nullable = false)
  private long createdAt;

  @Column(name = "updated_at", nullable = false)
  private long updatedAt;

  @Column(name = "last_accessed_at", nullable = false)
  private long lastAccessedAt;

  @Column(name = "used_at")
  private Long usedAt;

  public static TokenEntity fromDomain(Token token) {
    return TokenEntity.builder()
        .tokenId(token.getId().id().toString())
        .token(token.getToken())
        .userId(token.getUserId())
        .tokenUse(token.getTokenUse())
        .expirationTime(token.getExpirationTime())
        .createdAt(token.getCreatedAt())
        .updatedAt(token.getUpdatedAt())
        .lastAccessedAt(token.getLastAccessedAt())
        .usedAt(token.getUsedAt())
        .build();
  }

  public Token toDomain() {
    return new Token(
        TokenID.of(java.util.UUID.fromString(tokenId)),
        token,
        userId,
        tokenUse,
        expirationTime,
        createdAt,
        updatedAt,
        lastAccessedAt,
        usedAt
    );
  }
}
