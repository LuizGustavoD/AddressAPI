package com.auth.server.domain.response.exceptions.authorizationExceptions;

public class RefreshTokenExpiredException extends RuntimeException {

  public RefreshTokenExpiredException(String message) {
    super(message);
  }

}