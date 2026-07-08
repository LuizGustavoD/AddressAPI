package com.address.service.infrastructure.http.dto;

import java.util.List;

public record JuridicPersonResponseDTO(
    String id,
    String name,
    List<AddressResponseDTO> addresses,
    ContactResponseDTO contact,
    String cnpj) {
}