package com.mail.service.domain.exceptions;

public class ExpiredTokenException extends RuntimeException {

  public ExpiredTokenException(String message) {
    super(message);
  }
  
}
