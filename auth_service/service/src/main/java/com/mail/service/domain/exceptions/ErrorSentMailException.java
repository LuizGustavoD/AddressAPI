package com.mail.service.domain.exceptions;

public class ErrorSentMailException extends RuntimeException {

  public ErrorSentMailException(String message) {
    super(message);
  }
  
}
