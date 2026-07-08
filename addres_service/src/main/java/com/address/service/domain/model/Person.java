package com.address.service.domain.model;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class Person {

  private PersonID id;
  private String name;
  private List<Address> addresses = new ArrayList<>();
  private Contact contact;
}
