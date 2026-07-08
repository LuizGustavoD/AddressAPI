package com.auth.server.infrastructure.http.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth.server.infrastructure.security.JwtKeyProcessors;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;

@RestController
public class JwksController {

  private final JwtKeyProcessors jwtKeyProcessors;

  public JwksController(JwtKeyProcessors jwtKeyProcessors) {
    this.jwtKeyProcessors = jwtKeyProcessors;
  }

  @GetMapping("/.well-known/jwks.json")
  public Map<String, Object> jwks() {
    RSAKey jwk = new RSAKey.Builder(jwtKeyProcessors.getProcessPublicKey())
        .keyID("auth-key-id")
        .build();
    return new JWKSet(jwk).toJSONObject();
  }
}
