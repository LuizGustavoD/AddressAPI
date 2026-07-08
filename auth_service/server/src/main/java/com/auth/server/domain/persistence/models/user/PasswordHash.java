package com.auth.server.domain.persistence.models.user;

import com.auth.server.domain.response.exceptions.authExceptions.InvalidCredentialsException;

import lombok.Getter;

@Getter
public class PasswordHash {

  private final String value;

  public PasswordHash(String hashedValue) {
    if (hashedValue == null || hashedValue.trim().isEmpty()) {
      throw new InvalidCredentialsException("Invalid credentials provided");
    }
    this.value = hashedValue;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof PasswordHash)) return false;
    return value.equals(((PasswordHash) o).value);
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }

  @Override
  public String toString() {
    return "PasswordHash(****)";
  }

}
