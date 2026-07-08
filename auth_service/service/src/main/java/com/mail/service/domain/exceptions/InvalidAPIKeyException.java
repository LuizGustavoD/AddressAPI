package com.mail.service.domain.exceptions;

public class InvalidAPIKeyException extends RuntimeException {

  public InvalidAPIKeyException(String message) {
    super(message);
  }
  
}
