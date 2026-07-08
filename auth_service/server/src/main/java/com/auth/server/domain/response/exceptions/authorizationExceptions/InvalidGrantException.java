package com.auth.server.domain.response.exceptions.authorizationExceptions;

public class InvalidGrantException extends RuntimeException {

  public InvalidGrantException(String message) {
    super(message);
  }
  
}
