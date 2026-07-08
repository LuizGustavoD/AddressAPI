package com.address.service.infrastructure.persistence.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.address.service.domain.model.Contact;
import com.address.service.domain.model.ContactID;
import com.address.service.domain.persistence.repository.ContactRepository;
import com.address.service.infrastructure.persistence.jpa.ContactJpaRepository;
import com.address.service.infrastructure.persistence.mapper.DomainEntityMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class JpaContactRepositoryAdapter implements ContactRepository {

  private final ContactJpaRepository contactJpaRepository;

  @Override
  public Contact save(Contact contact) {
    return DomainEntityMapper.toDomain(
        contactJpaRepository.save(DomainEntityMapper.toEntity(contact))
    );
  }

  @Override
  public Optional<Contact> findById(ContactID id) {
    return contactJpaRepository.findById(id.id()).map(DomainEntityMapper::toDomain);
  }

  @Override
  public Optional<Contact> findByUserId(String userId) {
    return contactJpaRepository.findByUserId(userId).map(DomainEntityMapper::toDomain);
  }

  @Override
  public void deleteById(ContactID id) {
    contactJpaRepository.deleteById(id.id());
  }
}
