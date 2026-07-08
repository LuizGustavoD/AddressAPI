package com.auth.server.application.ports;

public interface PasswordHasher {

  String encode(String rawPassword);

  boolean matches(String rawPassword, String encodedPassword);
}