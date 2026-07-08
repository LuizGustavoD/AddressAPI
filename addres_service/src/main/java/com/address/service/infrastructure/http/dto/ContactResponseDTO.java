package com.address.service.infrastructure.http.dto;

public record ContactResponseDTO(
    String id,
    String userId,
    String name,
    String email,
    String phone) {
}