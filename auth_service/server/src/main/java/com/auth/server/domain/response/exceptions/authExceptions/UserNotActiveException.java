package com.auth.server.domain.response.exceptions.authExceptions;

public class UserNotActiveException extends RuntimeException {

  public UserNotActiveException(String message) {
    super(message);
  }
  
}
