package com.address.service.infrastructure.persistence.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.address.service.domain.model.JuridicPerson;
import com.address.service.domain.model.Person;
import com.address.service.domain.model.PersonID;
import com.address.service.domain.persistence.repository.JuridicPersonRepository;
import com.address.service.infrastructure.persistence.entities.JuridicPersonEntity;
import com.address.service.infrastructure.persistence.jpa.JuridicPersonJpaRepository;
import com.address.service.infrastructure.persistence.mapper.DomainEntityMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class JpaJuridicPersonRepositoryAdapter implements JuridicPersonRepository {

  private final JuridicPersonJpaRepository juridicPersonJpaRepository;

  @Override
  public JuridicPerson save(JuridicPerson person) {
    JuridicPersonEntity entity = (JuridicPersonEntity) DomainEntityMapper.toEntity(person);
    return (JuridicPerson) DomainEntityMapper.toDomain(juridicPersonJpaRepository.save(entity));
  }

  @Override
  public Optional<JuridicPerson> findById(PersonID id) {
    return juridicPersonJpaRepository.findById(id.id()).map(this::mapToJuridicDomain);
  }

  @Override
  public Optional<JuridicPerson> findByCnpj(String cnpj) {
    return juridicPersonJpaRepository.findByCnpj(cnpj).map(this::mapToJuridicDomain);
  }

  @Override
  public void deleteById(PersonID id) {
    juridicPersonJpaRepository.deleteById(id.id());
  }

  private JuridicPerson mapToJuridicDomain(JuridicPersonEntity entity) {
    Person person = DomainEntityMapper.toDomain(entity);
    return (JuridicPerson) person;
  }
}
