package com.address.service.domain.response.exceptions.securityExceptions;

public class MissingTokenClaimException extends RuntimeException {

  public MissingTokenClaimException(String message) {
    super(message);
  }
}