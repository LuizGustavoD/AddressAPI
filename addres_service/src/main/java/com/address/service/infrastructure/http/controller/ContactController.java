package com.address.service.infrastructure.http.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.address.service.application.response.ResponsePayload;
import com.address.service.application.service.ContactUseCaseService;
import com.address.service.domain.model.Contact;
import com.address.service.domain.model.ContactID;
import com.address.service.domain.response.exceptions.resourceExceptions.ContactNotFoundException;
import com.address.service.infrastructure.http.dto.ContactRequestDTO;
import com.address.service.infrastructure.http.dto.ContactResponseDTO;
import com.address.service.infrastructure.http.mapper.HttpDtoMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/contacts")
@RequiredArgsConstructor
public class ContactController {

	private final ContactUseCaseService contactUseCaseService;

	@PostMapping
	public ResponseEntity<ResponsePayload<ContactResponseDTO>> create(@Valid @RequestBody ContactRequestDTO request) {
		Contact created = contactUseCaseService.create(HttpDtoMapper.toDomain(request));
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ResponsePayload.success(HttpDtoMapper.toResponse(created), HttpStatus.CREATED.value(), "Contact created successfully"));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ResponsePayload<ContactResponseDTO>> findById(@PathVariable String id) {
		Contact contact = contactUseCaseService.findById(ContactID.fromString(id))
				.orElseThrow(() -> new ContactNotFoundException("Contact not found: " + id));

		return ResponseEntity.ok(ResponsePayload.success(HttpDtoMapper.toResponse(contact), HttpStatus.OK.value(), "Contact retrieved successfully"));
	}

	@GetMapping("/user/{userId}")
	public ResponseEntity<ResponsePayload<ContactResponseDTO>> findByUserId(@PathVariable String userId) {
		Optional<Contact> contact = contactUseCaseService.findByUserId(userId);
		Contact found = contact.orElseThrow(() -> new ContactNotFoundException("Contact not found for userId: " + userId));
		return ResponseEntity.ok(ResponsePayload.success(HttpDtoMapper.toResponse(found), HttpStatus.OK.value(), "Contact retrieved successfully"));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ResponsePayload<ContactResponseDTO>> update(@PathVariable String id, @Valid @RequestBody ContactRequestDTO request) {
		Contact updated = contactUseCaseService.update(ContactID.fromString(id), HttpDtoMapper.toDomain(request));
		return ResponseEntity.ok(ResponsePayload.success(HttpDtoMapper.toResponse(updated), HttpStatus.OK.value(), "Contact updated successfully"));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ResponsePayload<Void>> delete(@PathVariable String id) {
		contactUseCaseService.delete(ContactID.fromString(id));
		return ResponseEntity.ok(ResponsePayload.success(null, HttpStatus.OK.value(), "Contact deleted successfully"));
	}
}
