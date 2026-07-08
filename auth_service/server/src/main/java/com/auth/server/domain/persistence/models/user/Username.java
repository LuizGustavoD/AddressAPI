package com.auth.server.domain.persistence.models.user;

import com.auth.server.domain.response.exceptions.authExceptions.InvalidUsernameException;

import lombok.Getter;

@Getter
public class Username {

  private final String value;
  private static final int MIN_LENGTH = 3;
  private static final int MAX_LENGTH = 50;

  public Username(String value) {
    if (value == null || value.trim().isEmpty()) {
      throw new InvalidUsernameException("Username cannot be empty");
    }
    if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
      throw new InvalidUsernameException(
          String.format("Username must be between %d and %d characters", MIN_LENGTH, MAX_LENGTH));
    }
    if (!value.matches("^[a-zA-Z0-9_.-]+$")) {
      throw new InvalidUsernameException(
          "Username can only contain alphanumeric characters, underscores, dots and hyphens");
    }
    this.value = value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Username)) return false;
    return value.equals(((Username) o).value);
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }

  @Override
  public String toString() {
    return value;
  }

}
