package com.address.service.domain.response.exceptions.resourceExceptions;

public class ContactNotFoundException extends RuntimeException {

  public ContactNotFoundException(String message) {
    super(message);
  }
}
