package com.address.service.application.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.address.service.domain.model.Contact;
import com.address.service.domain.model.ContactID;
import com.address.service.domain.persistence.repository.ContactRepository;
import com.address.service.domain.response.exceptions.resourceExceptions.ContactAlreadyExistsException;
import com.address.service.domain.response.exceptions.resourceExceptions.ContactNotFoundException;
import com.address.service.domain.services.ContactUseCase;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContactUseCaseService implements ContactUseCase {

  private final ContactRepository contactRepository;

  @Override
  @Transactional
  public Contact create(Contact contact) {
    contactRepository.findByUserId(contact.getUserId())
        .ifPresent(existing -> {
          throw new ContactAlreadyExistsException("Contact already exists for userId: " + contact.getUserId());
        });

    if (contact.getId() == null) {
      contact.setId(ContactID.generate());
    }
    return contactRepository.save(contact);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Contact> findById(ContactID id) {
    return contactRepository.findById(id);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Contact> findByUserId(String userId) {
    return contactRepository.findByUserId(userId);
  }

  @Override
  @Transactional
  public Contact update(ContactID id, Contact contact) {
    Contact persisted = contactRepository.findById(id)
        .orElseThrow(() -> new ContactNotFoundException("Contact not found: " + id.id()));

    persisted.setUserId(contact.getUserId());
    persisted.setName(contact.getName());
    persisted.setEmail(contact.getEmail());
    persisted.setPhone(contact.getPhone());

    return contactRepository.save(persisted);
  }

  @Override
  @Transactional
  public void delete(ContactID id) {
    contactRepository.findById(id)
        .orElseThrow(() -> new ContactNotFoundException("Contact not found: " + id.id()));
    contactRepository.deleteById(id);
  }
}
