package com.address.service.infrastructure.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DiscriminatorValue("JURIDIC")
public class JuridicPersonEntity extends PersonEntity {

  @Column(nullable = true, length = 18, unique = true)
  private String cnpj;
}
