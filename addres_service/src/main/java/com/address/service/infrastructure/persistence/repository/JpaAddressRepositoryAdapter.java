package com.address.service.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.address.service.domain.model.Address;
import com.address.service.domain.model.AddressID;
import com.address.service.domain.model.PersonID;
import com.address.service.domain.persistence.repository.AddressRepository;
import com.address.service.infrastructure.persistence.entities.AddressEntity;
import com.address.service.infrastructure.persistence.jpa.AddressJpaRepository;
import com.address.service.infrastructure.persistence.jpa.PersonJpaRepository;
import com.address.service.infrastructure.persistence.mapper.DomainEntityMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class JpaAddressRepositoryAdapter implements AddressRepository {

  private final AddressJpaRepository addressJpaRepository;
  private final PersonJpaRepository personJpaRepository;

  @Override
  public Address save(Address address, PersonID personId) {
    AddressEntity entity = DomainEntityMapper.toEntity(address);
    entity.setPerson(personJpaRepository.getReferenceById(personId.id()));
    return DomainEntityMapper.toDomain(addressJpaRepository.save(entity));
  }

  @Override
  public Optional<Address> findById(AddressID id) {
    return addressJpaRepository.findById(id.id()).map(DomainEntityMapper::toDomain);
  }

  @Override
  public List<Address> findByPersonId(PersonID personId) {
    return addressJpaRepository.findByPersonId(personId.id())
        .stream()
        .map(DomainEntityMapper::toDomain)
        .toList();
  }

  @Override
  public void deleteById(AddressID id) {
    addressJpaRepository.deleteById(id.id());
  }
}
