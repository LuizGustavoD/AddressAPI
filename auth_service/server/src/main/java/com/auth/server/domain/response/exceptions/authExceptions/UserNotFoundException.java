package com.auth.server.domain.response.exceptions.authExceptions;

public class UserNotFoundException extends RuntimeException {

  public UserNotFoundException(String message) {
    super(message);
  }
  
}
