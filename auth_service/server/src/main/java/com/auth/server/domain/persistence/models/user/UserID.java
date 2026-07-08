package com.auth.server.domain.persistence.models.user;

public record UserID(String id) {

  public static UserID of(String id) {
    return new UserID(id);
  }

  public static UserID generate() {
    return new UserID(java.util.UUID.randomUUID().toString());
  }
}