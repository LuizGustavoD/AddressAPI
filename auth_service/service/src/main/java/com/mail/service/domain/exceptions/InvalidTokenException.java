package com.mail.service.domain.exceptions;

public class InvalidTokenException extends RuntimeException {
  
  public InvalidTokenException(String message) {
    super(message);
  }
}
