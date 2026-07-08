package com.auth.server.domain.persistence.models.user;

import com.auth.server.domain.response.exceptions.authExceptions.InvalidCredentialsException;

import lombok.Getter;

@Getter
public class Email {

  private final String value;

  public Email(String value) {
    if (value == null || value.trim().isEmpty()) {
      throw new InvalidCredentialsException("Invalid credentials provided");
    }
    if (!isValidEmail(value)) {
      throw new InvalidCredentialsException("Invalid credentials provided");
    }
    this.value = value.toLowerCase();
  }

  private boolean isValidEmail(String email) {
    return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Email)) return false;
    return value.equals(((Email) o).value);
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
