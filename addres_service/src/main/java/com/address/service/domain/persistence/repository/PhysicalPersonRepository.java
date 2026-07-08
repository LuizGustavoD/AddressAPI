package com.address.service.domain.persistence.repository;

import java.util.Optional;

import com.address.service.domain.model.PersonID;
import com.address.service.domain.model.PhysicalPerson;

public interface PhysicalPersonRepository {

  PhysicalPerson save(PhysicalPerson person);

  Optional<PhysicalPerson> findById(PersonID id);

  Optional<PhysicalPerson> findByCpf(String cpf);

  void deleteById(PersonID id);
}
