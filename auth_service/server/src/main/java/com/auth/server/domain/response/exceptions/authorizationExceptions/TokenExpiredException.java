package com.auth.server.domain.response.exceptions.authorizationExceptions;

public class TokenExpiredException extends RuntimeException {

  public TokenExpiredException(String message) {
    super(message);
  }
  
}
