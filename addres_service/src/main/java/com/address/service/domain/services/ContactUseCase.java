package com.address.service.domain.services;

import java.util.Optional;

import com.address.service.domain.model.Contact;
import com.address.service.domain.model.ContactID;

public interface ContactUseCase {

  Contact create(Contact contact);

  Optional<Contact> findById(ContactID id);

  Optional<Contact> findByUserId(String userId);

  Contact update(ContactID id, Contact contact);

  void delete(ContactID id);
}
