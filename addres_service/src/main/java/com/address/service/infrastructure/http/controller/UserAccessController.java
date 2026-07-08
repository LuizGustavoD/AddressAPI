package com.address.service.infrastructure.http.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import com.address.service.application.response.ResponsePayload;
import com.address.service.domain.model.auth.UserAccess;
import com.address.service.domain.services.MyProfileUseCase;
import com.address.service.infrastructure.http.dto.UserProfileResponseDTO;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/me")
@RestController
@RequiredArgsConstructor
public class UserAccessController {

  private final MyProfileUseCase myProfileUseCase;

  @GetMapping
  public ResponseEntity<ResponsePayload<UserProfileResponseDTO>> getMyProfile() {
    UserAccess userAccess = myProfileUseCase.getMyProfile();
    UserProfileResponseDTO response = new UserProfileResponseDTO(
        userAccess.userId().toString(),
        userAccess.userName(),
        userAccess.email());

    return ResponseEntity.ok(ResponsePayload.success(response, HttpStatus.OK.value(), "User profile retrieved successfully"));
  }
  
}
