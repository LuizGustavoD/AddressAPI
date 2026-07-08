package com.address.service.infrastructure.http.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ContactRequestDTO(
    @NotBlank String userId,
    @NotBlank String name,
    @NotBlank @Email String email,
    @NotBlank String phone) {
}