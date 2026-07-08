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
import com.address.service.application.service.PersonUseCaseService;
import com.address.service.domain.model.JuridicPerson;
import com.address.service.domain.model.PersonID;
import com.address.service.domain.model.PhysicalPerson;
import com.address.service.domain.response.exceptions.resourceExceptions.JuridicPersonNotFoundException;
import com.address.service.domain.response.exceptions.resourceExceptions.PhysicalPersonNotFoundException;
import com.address.service.infrastructure.http.dto.JuridicPersonRequestDTO;
import com.address.service.infrastructure.http.dto.JuridicPersonResponseDTO;
import com.address.service.infrastructure.http.dto.PhysicalPersonRequestDTO;
import com.address.service.infrastructure.http.dto.PhysicalPersonResponseDTO;
import com.address.service.infrastructure.http.mapper.HttpDtoMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/persons")
@RequiredArgsConstructor
public class PersonController {

	private final PersonUseCaseService personUseCaseService;

	@PostMapping("/physical")
	public ResponseEntity<ResponsePayload<PhysicalPersonResponseDTO>> createPhysical(@Valid @RequestBody PhysicalPersonRequestDTO request) {
		PhysicalPerson created = personUseCaseService.createPhysical(HttpDtoMapper.toDomain(request));
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ResponsePayload.success(HttpDtoMapper.toResponse(created), HttpStatus.CREATED.value(), "Physical person created successfully"));
	}

	@GetMapping("/physical/{id}")
	public ResponseEntity<ResponsePayload<PhysicalPersonResponseDTO>> findPhysicalById(@PathVariable String id) {
		PhysicalPerson person = personUseCaseService.findPhysicalById(PersonID.fromString(id))
				.orElseThrow(() -> new PhysicalPersonNotFoundException("Physical person not found: " + id));

		return ResponseEntity.ok(ResponsePayload.success(HttpDtoMapper.toResponse(person), HttpStatus.OK.value(), "Physical person retrieved successfully"));
	}

	@GetMapping("/physical/cpf/{cpf}")
	public ResponseEntity<ResponsePayload<PhysicalPersonResponseDTO>> findPhysicalByCpf(@PathVariable String cpf) {
		Optional<PhysicalPerson> person = personUseCaseService.findPhysicalByCpf(cpf);
		PhysicalPerson found = person.orElseThrow(() -> new PhysicalPersonNotFoundException("Physical person not found for cpf: " + cpf));
		return ResponseEntity.ok(ResponsePayload.success(HttpDtoMapper.toResponse(found), HttpStatus.OK.value(), "Physical person retrieved successfully"));
	}

	@PutMapping("/physical/{id}")
	public ResponseEntity<ResponsePayload<PhysicalPersonResponseDTO>> updatePhysical(
			@PathVariable String id,
			@Valid @RequestBody PhysicalPersonRequestDTO request) {
		PhysicalPerson updated = personUseCaseService.updatePhysical(PersonID.fromString(id), HttpDtoMapper.toDomain(request));
		return ResponseEntity.ok(ResponsePayload.success(HttpDtoMapper.toResponse(updated), HttpStatus.OK.value(), "Physical person updated successfully"));
	}

	@DeleteMapping("/physical/{id}")
	public ResponseEntity<ResponsePayload<Void>> deletePhysical(@PathVariable String id) {
		personUseCaseService.deletePhysical(PersonID.fromString(id));
		return ResponseEntity.ok(ResponsePayload.success(null, HttpStatus.OK.value(), "Physical person deleted successfully"));
	}

	@PostMapping("/juridic")
	public ResponseEntity<ResponsePayload<JuridicPersonResponseDTO>> createJuridic(@Valid @RequestBody JuridicPersonRequestDTO request) {
		JuridicPerson created = personUseCaseService.createJuridic(HttpDtoMapper.toDomain(request));
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ResponsePayload.success(HttpDtoMapper.toResponse(created), HttpStatus.CREATED.value(), "Juridic person created successfully"));
	}

	@GetMapping("/juridic/{id}")
	public ResponseEntity<ResponsePayload<JuridicPersonResponseDTO>> findJuridicById(@PathVariable String id) {
		JuridicPerson person = personUseCaseService.findJuridicById(PersonID.fromString(id))
				.orElseThrow(() -> new JuridicPersonNotFoundException("Juridic person not found: " + id));

		return ResponseEntity.ok(ResponsePayload.success(HttpDtoMapper.toResponse(person), HttpStatus.OK.value(), "Juridic person retrieved successfully"));
	}

	@GetMapping("/juridic/cnpj/{cnpj}")
	public ResponseEntity<ResponsePayload<JuridicPersonResponseDTO>> findJuridicByCnpj(@PathVariable String cnpj) {
		Optional<JuridicPerson> person = personUseCaseService.findJuridicByCnpj(cnpj);
		JuridicPerson found = person.orElseThrow(() -> new JuridicPersonNotFoundException("Juridic person not found for cnpj: " + cnpj));
		return ResponseEntity.ok(ResponsePayload.success(HttpDtoMapper.toResponse(found), HttpStatus.OK.value(), "Juridic person retrieved successfully"));
	}

	@PutMapping("/juridic/{id}")
	public ResponseEntity<ResponsePayload<JuridicPersonResponseDTO>> updateJuridic(
			@PathVariable String id,
			@Valid @RequestBody JuridicPersonRequestDTO request) {
		JuridicPerson updated = personUseCaseService.updateJuridic(PersonID.fromString(id), HttpDtoMapper.toDomain(request));
		return ResponseEntity.ok(ResponsePayload.success(HttpDtoMapper.toResponse(updated), HttpStatus.OK.value(), "Juridic person updated successfully"));
	}

	@DeleteMapping("/juridic/{id}")
	public ResponseEntity<ResponsePayload<Void>> deleteJuridic(@PathVariable String id) {
		personUseCaseService.deleteJuridic(PersonID.fromString(id));
		return ResponseEntity.ok(ResponsePayload.success(null, HttpStatus.OK.value(), "Juridic person deleted successfully"));
	}
}
