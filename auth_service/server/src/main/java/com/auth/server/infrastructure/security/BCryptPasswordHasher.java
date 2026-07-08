package com.auth.server.infrastructure.security;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.auth.server.application.ports.PasswordHasher;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BCryptPasswordHasher implements PasswordHasher {

  private final PasswordEncoder passwordEncoder;

  @Override
  public String encode(String rawPassword) {
    return passwordEncoder.encode(rawPassword);
  }

  @Override
  public boolean matches(String rawPassword, String encodedPassword) {
    return passwordEncoder.matches(rawPassword, encodedPassword);
  }
}