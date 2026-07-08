package com.auth.server.domain.response.exceptions.authExceptions;

public class InvalidUsernameException extends RuntimeException {

  public InvalidUsernameException(String message) {
    super(message);
  }

}
