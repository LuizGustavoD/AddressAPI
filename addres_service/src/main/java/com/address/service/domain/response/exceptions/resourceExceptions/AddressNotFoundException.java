package com.address.service.domain.response.exceptions.resourceExceptions;

public class AddressNotFoundException extends RuntimeException {

  public AddressNotFoundException(String message) {
    super(message);
  }
}
