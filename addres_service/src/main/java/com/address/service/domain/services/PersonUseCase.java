package com.address.service.domain.services;

import java.util.Optional;

import com.address.service.domain.model.JuridicPerson;
import com.address.service.domain.model.PersonID;
import com.address.service.domain.model.PhysicalPerson;

public interface PersonUseCase {

  PhysicalPerson createPhysical(PhysicalPerson person);

  Optional<PhysicalPerson> findPhysicalById(PersonID id);

  Optional<PhysicalPerson> findPhysicalByCpf(String cpf);

  PhysicalPerson updatePhysical(PersonID id, PhysicalPerson person);

  void deletePhysical(PersonID id);

  JuridicPerson createJuridic(JuridicPerson person);

  Optional<JuridicPerson> findJuridicById(PersonID id);

  Optional<JuridicPerson> findJuridicByCnpj(String cnpj);

  JuridicPerson updateJuridic(PersonID id, JuridicPerson person);

  void deleteJuridic(PersonID id);
}
