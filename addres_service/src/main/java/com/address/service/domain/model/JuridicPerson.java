package com.address.service.domain.model;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JuridicPerson extends Person {

  private String cnpj;

  public JuridicPerson(PersonID id, String name, List<Address> addresses, Contact contact, String cnpj) {
    super(id, name, addresses, contact);
    this.cnpj = cnpj;
  }
}
