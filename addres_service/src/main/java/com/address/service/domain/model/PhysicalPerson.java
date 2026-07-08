package com.address.service.domain.model;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PhysicalPerson extends Person {

  private String cpf;

  public PhysicalPerson(PersonID id, String name, List<Address> addresses, Contact contact, String cpf) {
    super(id, name, addresses, contact);
    this.cpf = cpf;
  }
}
