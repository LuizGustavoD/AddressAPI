package com.address.service.domain.services;

import java.util.List;
import java.util.Optional;

import com.address.service.domain.model.Address;
import com.address.service.domain.model.AddressID;
import com.address.service.domain.model.PersonID;

public interface AddressUseCase {

  Address create(Address address, PersonID personId);

  Optional<Address> findById(AddressID id);

  List<Address> findByPersonId(PersonID personId);

  Address update(AddressID id, Address address, PersonID personId);

  void delete(AddressID id);
}
