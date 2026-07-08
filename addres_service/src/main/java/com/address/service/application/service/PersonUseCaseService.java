package com.address.service.application.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.address.service.domain.model.JuridicPerson;
import com.address.service.domain.model.PersonID;
import com.address.service.domain.model.PhysicalPerson;
import com.address.service.domain.persistence.repository.JuridicPersonRepository;
import com.address.service.domain.persistence.repository.PhysicalPersonRepository;
import com.address.service.domain.response.exceptions.resourceExceptions.CnpjAlreadyExistsException;
import com.address.service.domain.response.exceptions.resourceExceptions.CpfAlreadyExistsException;
import com.address.service.domain.response.exceptions.resourceExceptions.JuridicPersonNotFoundException;
import com.address.service.domain.response.exceptions.resourceExceptions.PhysicalPersonNotFoundException;
import com.address.service.domain.services.PersonUseCase;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PersonUseCaseService implements PersonUseCase {

  private final PhysicalPersonRepository physicalPersonRepository;
  private final JuridicPersonRepository juridicPersonRepository;

  @Override
  @Transactional
  public PhysicalPerson createPhysical(PhysicalPerson person) {
    physicalPersonRepository.findByCpf(person.getCpf())
        .ifPresent(existing -> {
          throw new CpfAlreadyExistsException("CPF already exists: " + person.getCpf());
        });

    return physicalPersonRepository.save(person);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<PhysicalPerson> findPhysicalById(PersonID id) {
    return physicalPersonRepository.findById(id);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<PhysicalPerson> findPhysicalByCpf(String cpf) {
    return physicalPersonRepository.findByCpf(cpf);
  }

  @Override
  @Transactional
  public PhysicalPerson updatePhysical(PersonID id, PhysicalPerson person) {
    PhysicalPerson persisted = physicalPersonRepository.findById(id)
        .orElseThrow(() -> new PhysicalPersonNotFoundException("Physical person not found: " + id.id()));

    persisted.setName(person.getName());
    persisted.setCpf(person.getCpf());
    persisted.setContact(person.getContact());
    persisted.setAddresses(person.getAddresses());

    return physicalPersonRepository.save(persisted);
  }

  @Override
  @Transactional
  public void deletePhysical(PersonID id) {
    physicalPersonRepository.findById(id)
        .orElseThrow(() -> new PhysicalPersonNotFoundException("Physical person not found: " + id.id()));
    physicalPersonRepository.deleteById(id);
  }

  @Override
  @Transactional
  public JuridicPerson createJuridic(JuridicPerson person) {
    juridicPersonRepository.findByCnpj(person.getCnpj())
        .ifPresent(existing -> {
          throw new CnpjAlreadyExistsException("CNPJ already exists: " + person.getCnpj());
        });

    return juridicPersonRepository.save(person);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<JuridicPerson> findJuridicById(PersonID id) {
    return juridicPersonRepository.findById(id);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<JuridicPerson> findJuridicByCnpj(String cnpj) {
    return juridicPersonRepository.findByCnpj(cnpj);
  }

  @Override
  @Transactional
  public JuridicPerson updateJuridic(PersonID id, JuridicPerson person) {
    JuridicPerson persisted = juridicPersonRepository.findById(id)
        .orElseThrow(() -> new JuridicPersonNotFoundException("Juridic person not found: " + id.id()));

    persisted.setName(person.getName());
    persisted.setCnpj(person.getCnpj());
    persisted.setContact(person.getContact());
    persisted.setAddresses(person.getAddresses());

    return juridicPersonRepository.save(persisted);
  }

  @Override
  @Transactional
  public void deleteJuridic(PersonID id) {
    juridicPersonRepository.findById(id)
        .orElseThrow(() -> new JuridicPersonNotFoundException("Juridic person not found: " + id.id()));
    juridicPersonRepository.deleteById(id);
  }
}
