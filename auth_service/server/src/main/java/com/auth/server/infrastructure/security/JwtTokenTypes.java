package com.auth.server.infrastructure.security;

public enum JwtTokenTypes {
  ACCESS_TOKEN("access"),
  REFRESH_TOKEN("refresh"),
  CONFIRMATION_TOKEN("confirmation");

  private final String value;

  JwtTokenTypes(String value) {
    this.value = value;
  }

  public String value() {
    return value;
  }
}
