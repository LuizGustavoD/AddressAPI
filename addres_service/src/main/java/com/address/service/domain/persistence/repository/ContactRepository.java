package com.address.service.domain.persistence.repository;

import java.util.Optional;

import com.address.service.domain.model.Contact;
import com.address.service.domain.model.ContactID;

public interface ContactRepository {

	Contact save(Contact contact);

	Optional<Contact> findById(ContactID id);

	Optional<Contact> findByUserId(String userId);

	void deleteById(ContactID id);
}
