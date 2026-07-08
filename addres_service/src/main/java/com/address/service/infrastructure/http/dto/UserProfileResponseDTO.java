package com.address.service.infrastructure.http.dto;

public record UserProfileResponseDTO(
  String userId,
  String userName,
  String email
) {
  
}
