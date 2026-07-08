package com.address.service.domain.model;

import lombok.Data;

@Data
public class Contact {

  private ContactID id;
  private String userId;
  private String name;
  private String email;
  private String phone;

  public Contact(ContactID id, String userId, String name, String email, String phone) {
    this.id = id;
    this.userId = userId;
    this.name = name;
    this.email = email;
    this.phone = phone;
  }

  public Contact(String userId, String name, String email, String phone) {
    this.userId = userId;
    this.name = name;
    this.email = email;
    this.phone = phone;
  }

  public Contact() {
  }
}


