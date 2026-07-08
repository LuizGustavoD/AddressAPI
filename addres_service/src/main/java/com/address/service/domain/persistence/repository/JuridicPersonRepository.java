package com.address.service.domain.persistence.repository;

import java.util.Optional;

import com.address.service.domain.model.JuridicPerson;
import com.address.service.domain.model.PersonID;

public interface JuridicPersonRepository {

	JuridicPerson save(JuridicPerson person);

	Optional<JuridicPerson> findById(PersonID id);

	Optional<JuridicPerson> findByCnpj(String cnpj);

	void deleteById(PersonID id);
}
