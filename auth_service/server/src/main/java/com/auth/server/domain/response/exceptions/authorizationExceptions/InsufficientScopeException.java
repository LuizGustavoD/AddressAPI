package com.auth.server.domain.response.exceptions.authorizationExceptions;

public class InsufficientScopeException extends RuntimeException {

  public InsufficientScopeException(String message) {
    super(message);
  }
  
}
