package com.address.service.infrastructure.http.mapper;

import java.util.List;

import com.address.service.domain.model.Address;
import com.address.service.domain.model.Contact;
import com.address.service.domain.model.JuridicPerson;
import com.address.service.domain.model.PhysicalPerson;
import com.address.service.infrastructure.http.dto.AddressRequestDTO;
import com.address.service.infrastructure.http.dto.AddressResponseDTO;
import com.address.service.infrastructure.http.dto.ContactRequestDTO;
import com.address.service.infrastructure.http.dto.ContactResponseDTO;
import com.address.service.infrastructure.http.dto.JuridicPersonRequestDTO;
import com.address.service.infrastructure.http.dto.JuridicPersonResponseDTO;
import com.address.service.infrastructure.http.dto.PhysicalPersonRequestDTO;
import com.address.service.infrastructure.http.dto.PhysicalPersonResponseDTO;

public final class HttpDtoMapper {

  private HttpDtoMapper() {
  }

  public static Address toDomain(AddressRequestDTO dto) {
    Address address = new Address();
    address.setStreet(dto.street());
    address.setNumber(dto.number());
    address.setCity(dto.city());
    address.setCep(dto.cep());
    address.setState(dto.state());
    return address;
  }

  public static AddressResponseDTO toResponse(Address address) {
    String id = address.getId() == null ? null : address.getId().id().toString();
    return new AddressResponseDTO(id, address.getStreet(), address.getNumber(), address.getCity(), address.getCep(), address.getState());
  }

  public static Contact toDomain(ContactRequestDTO dto) {
    return new Contact(dto.userId(), dto.name(), dto.email(), dto.phone());
  }

  public static ContactResponseDTO toResponse(Contact contact) {
    String id = contact.getId() == null ? null : contact.getId().id().toString();
    return new ContactResponseDTO(id, contact.getUserId(), contact.getName(), contact.getEmail(), contact.getPhone());
  }

  public static PhysicalPerson toDomain(PhysicalPersonRequestDTO dto) {
    PhysicalPerson person = new PhysicalPerson();
    person.setName(dto.name());
    person.setCpf(dto.cpf());
    person.setAddresses(dto.addresses().stream().map(HttpDtoMapper::toDomain).toList());
    person.setContact(toDomain(dto.contact()));
    return person;
  }

  public static PhysicalPersonResponseDTO toResponse(PhysicalPerson person) {
    String id = person.getId() == null ? null : person.getId().id().toString();
    List<AddressResponseDTO> addresses = person.getAddresses() == null
        ? List.of()
        : person.getAddresses().stream().map(HttpDtoMapper::toResponse).toList();

    return new PhysicalPersonResponseDTO(id, person.getName(), addresses, person.getContact() == null ? null : toResponse(person.getContact()), person.getCpf());
  }

  public static JuridicPerson toDomain(JuridicPersonRequestDTO dto) {
    JuridicPerson person = new JuridicPerson();
    person.setName(dto.name());
    person.setCnpj(dto.cnpj());
    person.setAddresses(dto.addresses().stream().map(HttpDtoMapper::toDomain).toList());
    person.setContact(toDomain(dto.contact()));
    return person;
  }

  public static JuridicPersonResponseDTO toResponse(JuridicPerson person) {
    String id = person.getId() == null ? null : person.getId().id().toString();
    List<AddressResponseDTO> addresses = person.getAddresses() == null
        ? List.of()
        : person.getAddresses().stream().map(HttpDtoMapper::toResponse).toList();

    return new JuridicPersonResponseDTO(id, person.getName(), addresses, person.getContact() == null ? null : toResponse(person.getContact()), person.getCnpj());
  }
}