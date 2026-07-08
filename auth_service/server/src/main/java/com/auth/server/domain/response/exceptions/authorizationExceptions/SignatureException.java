package com.auth.server.domain.response.exceptions.authorizationExceptions;

public class SignatureException extends RuntimeException {

  public SignatureException(String message) {
    super(message);
  }
  
}
