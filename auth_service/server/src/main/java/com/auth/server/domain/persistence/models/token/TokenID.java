package com.auth.server.domain.persistence.models.token;

import java.util.UUID;

public record TokenID(
   UUID id
) {
  
  public static TokenID of(UUID id) {
    return new TokenID(id);
  }

  public static TokenID generate() {
    return new TokenID(UUID.randomUUID());
  }
}
