package com.address.service.infrastructure.http.controller;

import java.util.List;

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
import com.address.service.application.service.AddressUseCaseService;
import com.address.service.domain.model.Address;
import com.address.service.domain.model.AddressID;
import com.address.service.domain.model.PersonID;
import com.address.service.domain.response.exceptions.resourceExceptions.AddressNotFoundException;
import com.address.service.infrastructure.http.dto.AddressRequestDTO;
import com.address.service.infrastructure.http.dto.AddressResponseDTO;
import com.address.service.infrastructure.http.mapper.HttpDtoMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

  private final AddressUseCaseService addressUseCaseService;

  @PostMapping("/person/{personId}")
  public ResponseEntity<ResponsePayload<AddressResponseDTO>> create(
      @PathVariable String personId,
      @Valid @RequestBody AddressRequestDTO request) {
    Address created = addressUseCaseService.create(HttpDtoMapper.toDomain(request), PersonID.fromString(personId));
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ResponsePayload.success(HttpDtoMapper.toResponse(created), HttpStatus.CREATED.value(), "Address created successfully"));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ResponsePayload<AddressResponseDTO>> findById(@PathVariable String id) {
    Address address = addressUseCaseService.findById(AddressID.fromString(id))
        .orElseThrow(() -> new AddressNotFoundException("Address not found: " + id));

    return ResponseEntity.ok(ResponsePayload.success(HttpDtoMapper.toResponse(address), HttpStatus.OK.value(), "Address retrieved successfully"));
  }

  @GetMapping("/person/{personId}")
  public ResponseEntity<ResponsePayload<List<AddressResponseDTO>>> findByPersonId(@PathVariable String personId) {
    List<Address> addresses = addressUseCaseService.findByPersonId(PersonID.fromString(personId));
    List<AddressResponseDTO> response = addresses.stream().map(HttpDtoMapper::toResponse).toList();
    return ResponseEntity.ok(ResponsePayload.success(response, HttpStatus.OK.value(), "Addresses retrieved successfully"));
  }

  @PutMapping("/{id}/person/{personId}")
  public ResponseEntity<ResponsePayload<AddressResponseDTO>> update(
      @PathVariable String id,
      @PathVariable String personId,
      @Valid @RequestBody AddressRequestDTO request) {
    Address updated = addressUseCaseService.update(AddressID.fromString(id), HttpDtoMapper.toDomain(request), PersonID.fromString(personId));

    return ResponseEntity.ok(ResponsePayload.success(HttpDtoMapper.toResponse(updated), HttpStatus.OK.value(), "Address updated successfully"));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ResponsePayload<Void>> delete(@PathVariable String id) {
    addressUseCaseService.delete(AddressID.fromString(id));
    return ResponseEntity.ok(ResponsePayload.success(null, HttpStatus.OK.value(), "Address deleted successfully"));
  }
}
