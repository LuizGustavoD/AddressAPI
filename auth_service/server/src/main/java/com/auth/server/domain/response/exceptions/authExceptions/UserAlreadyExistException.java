package com.auth.server.domain.response.exceptions.authExceptions;

public class UserAlreadyExistException extends RuntimeException {

  public UserAlreadyExistException(String message) {
    super(message);
  }
  
}
