package com.auth.server.domain.response.exceptions.authExceptions;

public class AccountLockedException extends RuntimeException {

  public AccountLockedException(String message) {
    super(message);
  }

}