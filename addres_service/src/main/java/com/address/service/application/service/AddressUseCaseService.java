package com.address.service.application.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.address.service.domain.model.Address;
import com.address.service.domain.model.AddressID;
import com.address.service.domain.model.PersonID;
import com.address.service.domain.persistence.repository.AddressRepository;
import com.address.service.domain.response.exceptions.resourceExceptions.AddressNotFoundException;
import com.address.service.domain.services.AddressUseCase;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AddressUseCaseService implements AddressUseCase {

  private final AddressRepository addressRepository;

  @Override
  @Transactional
  public Address create(Address address, PersonID personId) {
    if (address.getId() == null) {
      address.setId(AddressID.generate());
    }
    return addressRepository.save(address, personId);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Address> findById(AddressID id) {
    return addressRepository.findById(id);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Address> findByPersonId(PersonID personId) {
    return addressRepository.findByPersonId(personId);
  }

  @Override
  @Transactional
  public Address update(AddressID id, Address address, PersonID personId) {
    Address persisted = addressRepository.findById(id)
        .orElseThrow(() -> new AddressNotFoundException("Address not found: " + id.id()));

    persisted.setStreet(address.getStreet());
    persisted.setNumber(address.getNumber());
    persisted.setCity(address.getCity());
    persisted.setCep(address.getCep());
    persisted.setState(address.getState());

    return addressRepository.save(persisted, personId);
  }

  @Override
  @Transactional
  public void delete(AddressID id) {
    addressRepository.findById(id)
        .orElseThrow(() -> new AddressNotFoundException("Address not found: " + id.id()));
    addressRepository.deleteById(id);
  }
}
