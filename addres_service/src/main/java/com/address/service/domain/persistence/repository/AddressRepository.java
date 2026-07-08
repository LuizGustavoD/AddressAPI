package com.address.service.domain.persistence.repository;

import java.util.List;
import java.util.Optional;

import com.address.service.domain.model.Address;
import com.address.service.domain.model.AddressID;
import com.address.service.domain.model.PersonID;

public interface AddressRepository {

	Address save(Address address, PersonID personId);

	Optional<Address> findById(AddressID id);

	List<Address> findByPersonId(PersonID personId);

	void deleteById(AddressID id);
}
