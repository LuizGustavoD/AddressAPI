package com.address.service.domain.response.exceptions.resourceExceptions;

public class ContactAlreadyExistsException extends RuntimeException {

  public ContactAlreadyExistsException(String message) {
    super(message);
  }
}
