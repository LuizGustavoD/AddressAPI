package com.auth.server.domain.persistence.models.user;

import com.auth.server.domain.response.exceptions.authExceptions.InvalidCredentialsException;
import com.auth.server.domain.response.exceptions.authExceptions.UserNotActiveException;

import lombok.Getter;

@Getter
public class User {

  private final UserID id;
  private final Username username;
  private final PasswordHash password;
  private final Email email;
  private boolean isActive;

  public UserID getId() {
    return id;
  }

  public Username getUsername() {
    return username;
  }

  public PasswordHash getPassword() {
    return password;
  }

  public Email getEmail() {
    return email;
  }

  public boolean isActive() {
    return isActive;
  }

  public User(UserID id, Username username, PasswordHash password, Email email) {
    this.id = id;
    this.username = username;
    this.password = password;
    this.email = email;
    this.isActive = false;
  }


  public void activate() {
    this.isActive = true;
  }

  public void deactivate() {
    this.isActive = false;
  }

  public void validateIsActive() {
    if (!isActive) {
      throw new UserNotActiveException("User account is not active. Please confirm your email.");
    }
  }

  public void changeEmail(Email newEmail) {
    if (newEmail == null) {
      throw new InvalidCredentialsException("Email cannot be null");
    }
  }

  public void changePassword(PasswordHash newPassword) {
    if (newPassword == null) {
      throw new InvalidCredentialsException("Password cannot be null");
    }
  }

}