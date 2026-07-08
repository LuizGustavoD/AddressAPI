package com.auth.server.infrastructure.http.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestHeader;
import com.auth.server.domain.response.exceptions.authExceptions.InvalidCredentialsException;
import com.auth.server.application.dto.AuthResponse;
import com.auth.server.application.dto.LoginCommand;
import com.auth.server.application.dto.RegisterCommand;
import com.auth.server.application.dto.UserResponse;
import com.auth.server.application.response.ResponsePayload;
import com.auth.server.application.services.ConfirmRegistrationUseCase;
import com.auth.server.application.services.LoginUseCase;
import com.auth.server.application.services.RegisterUseCase;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {

  private final RegisterUseCase registerUseCase;
  private final LoginUseCase loginUseCase;
  private final ConfirmRegistrationUseCase confirmRegistrationUseCase;
  private final String serverApiKey;

  public AuthController(
      RegisterUseCase registerUseCase,
      LoginUseCase loginUseCase,
      ConfirmRegistrationUseCase confirmRegistrationUseCase,
      @Value("${auth-server.api-key:super-secret-auth-api-key}") String serverApiKey) {
    this.registerUseCase = registerUseCase;
    this.loginUseCase = loginUseCase;
    this.confirmRegistrationUseCase = confirmRegistrationUseCase;
    this.serverApiKey = serverApiKey;
  }

  @PostMapping("/register")
  public ResponseEntity<ResponsePayload<UserResponse>> register(@Valid @RequestBody RegisterCommand command) {
    UserResponse response = registerUseCase.execute(command);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(ResponsePayload.success(response, HttpStatus.CREATED.value(), "User registered successfully"));
  }

  @PostMapping("/login")
  public ResponseEntity<ResponsePayload<AuthResponse>> login(@Valid @RequestBody LoginCommand command) {
    AuthResponse response = loginUseCase.execute(command);
    return ResponseEntity
        .ok(ResponsePayload.success(response, HttpStatus.OK.value(), "Login successful"));
  }

  @GetMapping("/confirm")
  public ResponseEntity<ResponsePayload<UserResponse>> confirm(@RequestParam("token") String token) {
    UserResponse response = confirmRegistrationUseCase.execute(token);
    return ResponseEntity
        .ok(ResponsePayload.success(response, HttpStatus.OK.value(), "User confirmed successfully"));
  }

  @PostMapping("/activate")
  public ResponseEntity<ResponsePayload<UserResponse>> activate(
      @RequestHeader("X-API-KEY") String apiKey,
      @RequestParam("token") String token) {
    if (serverApiKey == null || serverApiKey.isBlank() || !serverApiKey.equals(apiKey)) {
      throw new InvalidCredentialsException("Invalid API Key");
    }
    UserResponse response = confirmRegistrationUseCase.execute(token);
    return ResponseEntity
        .ok(ResponsePayload.success(response, HttpStatus.OK.value(), "Account activated successfully"));
  }

}
