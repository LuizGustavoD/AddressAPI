package com.address.service.domain.model;

import lombok.Data;

@Data
public class Address {

  private AddressID id;

  private String street;

  private int number;

  private String city;

  private String cep;

  private String state;

}


