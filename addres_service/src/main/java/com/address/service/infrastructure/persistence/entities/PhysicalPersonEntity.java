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
@DiscriminatorValue("PHYSICAL")
public class PhysicalPersonEntity extends PersonEntity {

  @Column(nullable = true, length = 14, unique = true)
  private String cpf;
}
