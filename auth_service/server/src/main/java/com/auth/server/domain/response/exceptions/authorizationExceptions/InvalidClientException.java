package com.auth.server.domain.response.exceptions.authorizationExceptions;

public class InvalidClientException extends RuntimeException {

  public InvalidClientException(String message) {
    super(message);
  }
  
}
