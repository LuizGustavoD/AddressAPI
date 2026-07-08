package com.address.service.infrastructure.http.dto;

import java.util.List;

public record PhysicalPersonResponseDTO(
    String id,
    String name,
    List<AddressResponseDTO> addresses,
    ContactResponseDTO contact,
    String cpf) {
}