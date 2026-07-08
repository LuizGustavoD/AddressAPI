package com.address.service.infrastructure.persistence.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.address.service.domain.model.Person;
import com.address.service.domain.model.PersonID;
import com.address.service.domain.model.PhysicalPerson;
import com.address.service.domain.persistence.repository.PhysicalPersonRepository;
import com.address.service.infrastructure.persistence.entities.PhysicalPersonEntity;
import com.address.service.infrastructure.persistence.jpa.PhysicalPersonJpaRepository;
import com.address.service.infrastructure.persistence.mapper.DomainEntityMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class JpaPhysicalPersonRepositoryAdapter implements PhysicalPersonRepository {

  private final PhysicalPersonJpaRepository physicalPersonJpaRepository;

  @Override
  public PhysicalPerson save(PhysicalPerson person) {
    PhysicalPersonEntity entity = (PhysicalPersonEntity) DomainEntityMapper.toEntity(person);
    return (PhysicalPerson) DomainEntityMapper.toDomain(physicalPersonJpaRepository.save(entity));
  }

  @Override
  public Optional<PhysicalPerson> findById(PersonID id) {
    return physicalPersonJpaRepository.findById(id.id()).map(this::mapToPhysicalDomain);
  }

  @Override
  public Optional<PhysicalPerson> findByCpf(String cpf) {
    return physicalPersonJpaRepository.findByCpf(cpf).map(this::mapToPhysicalDomain);
  }

  @Override
  public void deleteById(PersonID id) {
    physicalPersonJpaRepository.deleteById(id.id());
  }

  private PhysicalPerson mapToPhysicalDomain(PhysicalPersonEntity entity) {
    Person person = DomainEntityMapper.toDomain(entity);
    return (PhysicalPerson) person;
  }
}
