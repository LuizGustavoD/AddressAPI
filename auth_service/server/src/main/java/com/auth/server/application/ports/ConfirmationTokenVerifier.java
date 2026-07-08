package com.auth.server.application.ports;

public interface ConfirmationTokenVerifier {
  VerifiedConfirmationToken verify(String token);
}