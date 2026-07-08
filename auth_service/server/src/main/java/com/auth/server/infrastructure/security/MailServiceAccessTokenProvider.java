package com.auth.server.infrastructure.security;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

@Component
public class MailServiceAccessTokenProvider {

  private final JwtEncoder jwtEncoder;

  @Value("${jwt.issuer:auth-service}")
  private String issuer;

  @Value("${jwt.mail-service-audience:mail-service}")
  private String mailServiceAudience;

  @Value("${jwt.service-token-expiration-seconds:120}")
  private long serviceTokenExpirationSeconds;

  public MailServiceAccessTokenProvider(JwtEncoder jwtEncoder) {
    this.jwtEncoder = jwtEncoder;
  }

  public String generateToken() {
    Instant issuedAt = Instant.now();
    Instant expiresAt = issuedAt.plusSeconds(serviceTokenExpirationSeconds);

    JwtClaimsSet claimsSet = JwtClaimsSet.builder()
        .id(UUID.randomUUID().toString())
        .issuer(issuer)
        .subject("auth-service")
        .issuedAt(issuedAt)
        .expiresAt(expiresAt)
        .audience(List.of(mailServiceAudience))
        .claim(JwtTokenClaims.TOKEN_USE.value(), JwtTokenTypes.ACCESS_TOKEN.value())
        .claim(JwtTokenClaims.USERNAME.value(), "auth-service")
        .build();

    JwsHeader jwsHeader = JwsHeader.with(SignatureAlgorithm.RS256).keyId("auth-key-id").build();
    return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claimsSet)).getTokenValue();
  }
}
