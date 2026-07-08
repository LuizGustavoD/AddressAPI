package com.address.service.domain.response.exceptions.resourceExceptions;

public class PhysicalPersonNotFoundException extends RuntimeException {

  public PhysicalPersonNotFoundException(String message) {
    super(message);
  }
}
