package com.address.service.infrastructure.http.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record AddressRequestDTO(
    @NotBlank String street,
    @Min(1) int number,
    @NotBlank String city,
    @NotBlank String cep,
    @NotBlank String state) {
}