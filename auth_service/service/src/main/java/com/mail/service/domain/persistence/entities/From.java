package com.mail.service.domain.persistence.entities;

public class From {

  private String email;
  private String name;

  public From(String email, String name) {
    this.email = email;
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public String getName() {
    return name;
  }
}
