package com.address.service.domain.model;

import java.util.UUID;

public record ContactID(
  UUID id
) {

  public static ContactID generate() {
    return new ContactID(UUID.randomUUID());
  }

  public static ContactID fromString(String id) {
    return new ContactID(UUID.fromString(id));
  }
}
