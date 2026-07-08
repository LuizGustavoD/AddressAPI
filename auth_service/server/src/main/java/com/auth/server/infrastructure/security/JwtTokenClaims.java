package com.auth.server.infrastructure.security;

public enum JwtTokenClaims {
  TOKEN_USE("token_use"),
  EMAIL("email"),
  USERNAME("username");

  private final String value;

  JwtTokenClaims(String value) {
    this.value = value;
  }

  public String value() {
    return value;
  }
}
