package com.address.service.domain.model;

import java.util.UUID;

public record AddressID(
  UUID id
) {
  public static AddressID generate() {
    return new AddressID(UUID.randomUUID());
  }

  public static AddressID fromString(String id) {
    return new AddressID(UUID.fromString(id));
  }

  public AddressID() {
    this(UUID.randomUUID());
  }

  public AddressID(String id) {
    this(UUID.fromString(id));
  }

  public AddressID(UUID id) {
    this.id = id;
  }
}
