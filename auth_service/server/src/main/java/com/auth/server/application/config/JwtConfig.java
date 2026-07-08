package com.auth.server.application.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import com.auth.server.infrastructure.security.JwtKeyProcessors;

import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class JwtConfig {
  
  private final JwtKeyProcessors jwtKeysProcessors;

  @Value("${jwt.issuer:auth-service}")
  private String issuer;

  @Bean
  public JwtEncoder jwtEncoder() {
    RSAKey rsaKey = new RSAKey.Builder(jwtKeysProcessors.getProcessPublicKey())
        .privateKey(jwtKeysProcessors.getProcessPrivateKey())
        .keyID("auth-key-id")
        .build();

    JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(rsaKey));
    return new NimbusJwtEncoder(jwkSource);
  }

  @Bean
  public JwtDecoder jwtDecoder() {
    NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withPublicKey(jwtKeysProcessors.getProcessPublicKey()).build();
    jwtDecoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(issuer));
    return jwtDecoder;
  }

  
}
