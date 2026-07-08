package com.address.service.domain.response.exceptions.securityExceptions;

public class InvalidAuthenticatedPrincipalException extends RuntimeException {

  public InvalidAuthenticatedPrincipalException(String message) {
    super(message);
  }
}