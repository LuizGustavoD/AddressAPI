package com.address.service.domain.response.exceptions.resourceExceptions;

public class CnpjAlreadyExistsException extends RuntimeException {

  public CnpjAlreadyExistsException(String message) {
    super(message);
  }
}
