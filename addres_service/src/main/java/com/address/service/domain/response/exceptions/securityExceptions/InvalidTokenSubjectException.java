package com.address.service.domain.response.exceptions.securityExceptions;

public class InvalidTokenSubjectException extends RuntimeException {

  public InvalidTokenSubjectException(String message) {
    super(message);
  }
}