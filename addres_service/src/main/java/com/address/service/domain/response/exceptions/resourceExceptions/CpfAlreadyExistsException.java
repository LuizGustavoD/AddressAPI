package com.address.service.domain.response.exceptions.resourceExceptions;

public class CpfAlreadyExistsException extends RuntimeException {

  public CpfAlreadyExistsException(String message) {
    super(message);
  }
}
