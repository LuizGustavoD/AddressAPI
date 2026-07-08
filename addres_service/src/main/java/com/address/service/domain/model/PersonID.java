package com.address.service.domain.model;

import java.util.UUID;

public record PersonID(
  UUID id
) {
  public static PersonID generate() {
    return new PersonID(UUID.randomUUID());
  }

  public static PersonID fromString(String id) {
    return new PersonID(UUID.fromString(id));
  }

  public PersonID() {
    this(UUID.randomUUID());
  }

  public PersonID(String id) {
    this(UUID.fromString(id));
  }
  public PersonID(UUID id) {
    this.id = id;
  }
}
