package com.address.service.infrastructure.http.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record JuridicPersonRequestDTO(
    @NotBlank String name,
    @NotBlank String cnpj,
    @NotNull List<@Valid AddressRequestDTO> addresses,
    @NotNull @Valid ContactRequestDTO contact) {
}