package com.address.service.infrastructure.persistence.mapper;

import java.util.ArrayList;
import java.util.List;

import com.address.service.domain.model.Address;
import com.address.service.domain.model.AddressID;
import com.address.service.domain.model.Contact;
import com.address.service.domain.model.ContactID;
import com.address.service.domain.model.JuridicPerson;
import com.address.service.domain.model.Person;
import com.address.service.domain.model.PersonID;
import com.address.service.domain.model.PhysicalPerson;
import com.address.service.infrastructure.persistence.entities.AddressEntity;
import com.address.service.infrastructure.persistence.entities.ContactEntity;
import com.address.service.infrastructure.persistence.entities.JuridicPersonEntity;
import com.address.service.infrastructure.persistence.entities.PersonEntity;
import com.address.service.infrastructure.persistence.entities.PhysicalPersonEntity;

public final class DomainEntityMapper {

  private DomainEntityMapper() {
  }

  public static Address toDomain(AddressEntity entity) {
    if (entity == null) {
      return null;
    }

    Address address = new Address();
    address.setId(entity.getId() == null ? null : new AddressID(entity.getId()));
    address.setStreet(entity.getStreet());
    address.setNumber(entity.getNumber() == null ? 0 : entity.getNumber());
    address.setCity(entity.getCity());
    address.setCep(entity.getCep());
    address.setState(entity.getState());
    return address;
  }

  public static AddressEntity toEntity(Address domain) {
    if (domain == null) {
      return null;
    }

    AddressEntity entity = new AddressEntity();
    if (domain.getId() != null) {
      entity.setId(domain.getId().id());
    }

    entity.setStreet(domain.getStreet());
    entity.setNumber(domain.getNumber());
    entity.setCity(domain.getCity());
    entity.setCep(domain.getCep());
    entity.setState(domain.getState());
    return entity;
  }

  public static Contact toDomain(ContactEntity entity) {
    if (entity == null) {
      return null;
    }

    Contact contact = new Contact();
    contact.setId(entity.getId() == null ? null : new ContactID(entity.getId()));
    contact.setUserId(entity.getUserId());
    contact.setName(entity.getName());
    contact.setEmail(entity.getEmail());
    contact.setPhone(entity.getPhone());
    return contact;
  }

  public static ContactEntity toEntity(Contact domain) {
    if (domain == null) {
      return null;
    }

    ContactEntity entity = new ContactEntity();
    if (domain.getId() != null) {
      entity.setId(domain.getId().id());
    }

    entity.setUserId(domain.getUserId());
    entity.setName(domain.getName());
    entity.setEmail(domain.getEmail());
    entity.setPhone(domain.getPhone());
    return entity;
  }

  public static Person toDomain(PersonEntity entity) {
    if (entity == null) {
      return null;
    }

    Person person;
    if (entity instanceof PhysicalPersonEntity physicalEntity) {
      PhysicalPerson physicalPerson = new PhysicalPerson();
      physicalPerson.setCpf(physicalEntity.getCpf());
      person = physicalPerson;
    } else if (entity instanceof JuridicPersonEntity juridicEntity) {
      JuridicPerson juridicPerson = new JuridicPerson();
      juridicPerson.setCnpj(juridicEntity.getCnpj());
      person = juridicPerson;
    } else {
      throw new IllegalArgumentException("Unsupported person entity type: " + entity.getClass().getName());
    }

    person.setId(entity.getId() == null ? null : new PersonID(entity.getId()));
    person.setName(entity.getName());
    person.setContact(toDomain(entity.getContact()));

    List<Address> addresses = new ArrayList<>();
    if (entity.getAddresses() != null) {
      for (AddressEntity addressEntity : entity.getAddresses()) {
        addresses.add(toDomain(addressEntity));
      }
    }
    person.setAddresses(addresses);

    return person;
  }

  public static PersonEntity toEntity(Person domain) {
    if (domain == null) {
      return null;
    }

    PersonEntity entity;
    if (domain instanceof PhysicalPerson physicalPerson) {
      PhysicalPersonEntity physicalEntity = new PhysicalPersonEntity();
      physicalEntity.setCpf(physicalPerson.getCpf());
      entity = physicalEntity;
    } else if (domain instanceof JuridicPerson juridicPerson) {
      JuridicPersonEntity juridicEntity = new JuridicPersonEntity();
      juridicEntity.setCnpj(juridicPerson.getCnpj());
      entity = juridicEntity;
    } else {
      throw new IllegalArgumentException("Unsupported person domain type: " + domain.getClass().getName());
    }

    if (domain.getId() != null) {
      entity.setId(domain.getId().id());
    }

    entity.setName(domain.getName());
    entity.setContact(toEntity(domain.getContact()));

    List<AddressEntity> addresses = new ArrayList<>();
    if (domain.getAddresses() != null) {
      for (Address address : domain.getAddresses()) {
        addresses.add(toEntity(address));
      }
    }
    entity.setAddresses(addresses);

    return entity;
  }
}
