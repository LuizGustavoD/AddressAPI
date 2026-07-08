package com.auth.server.domain.response.exceptions.authorizationExceptions;

public class InvalidTokenException extends RuntimeException {

  public InvalidTokenException(String message) {
    super(message);
  }

}