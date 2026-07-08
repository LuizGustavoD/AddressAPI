package com.address.service.domain.response.exceptions.securityExceptions;

public class AuthenticatedUserNotFoundException extends RuntimeException {

  public AuthenticatedUserNotFoundException(String message) {
    super(message);
  }
}