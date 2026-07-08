package com.address.service.infrastructure.http.dto;

public record AddressResponseDTO(
    String id,
    String street,
    int number,
    String city,
    String cep,
    String state) {
}