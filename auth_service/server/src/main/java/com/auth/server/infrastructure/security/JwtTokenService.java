package com.auth.server.infrastructure.security;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

import com.auth.server.application.ports.AccessTokenIssuer;
import com.auth.server.application.ports.ConfirmationTokenIssuer;
import com.auth.server.application.ports.ConfirmationTokenVerifier;
import com.auth.server.application.ports.IssuedAccessToken;
import com.auth.server.application.ports.RefreshTokenIssuer;
import com.auth.server.application.ports.VerifiedConfirmationToken;
import com.auth.server.domain.persistence.models.token.Token;
import com.auth.server.domain.persistence.models.token.TokenID;
import com.auth.server.domain.persistence.repository.TokenRepository;
import com.auth.server.domain.persistence.models.user.User;
import com.auth.server.domain.response.exceptions.authorizationExceptions.InvalidTokenException;
import com.auth.server.domain.response.exceptions.authorizationExceptions.TokenExpiredException;

@Service
public class JwtTokenService implements AccessTokenIssuer, RefreshTokenIssuer, ConfirmationTokenIssuer, ConfirmationTokenVerifier {

  private final JwtEncoder jwtEncoder;
  private final JwtDecoder jwtDecoder;
  private final TokenRepository tokenRepository;

  @Value("${jwt.issuer:auth-service}")
  private String issuer;

  @Value("${jwt.access-token-expiration-seconds:900}")
  private long accessTokenExpirationSeconds;

  @Value("${jwt.access-token-audience:address-service,mail-service}")
  private String accessTokenAudience;

  @Value("${jwt.refresh-token-expiration-seconds:604800}")
  private long refreshTokenExpirationSeconds;

  @Value("${jwt.confirmation-token-expiration-seconds:86400}")
  private long confirmationTokenExpirationSeconds;

  public JwtTokenService(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder, TokenRepository tokenRepository) {
    this.jwtEncoder = jwtEncoder;
    this.jwtDecoder = jwtDecoder;
    this.tokenRepository = tokenRepository;
  }

  @Override
  public IssuedAccessToken issueAccessTokenFor(User user) {
    String token = encode(user, JwtTokenTypes.ACCESS_TOKEN, accessTokenExpirationSeconds, TokenID.generate().id().toString());
    return new IssuedAccessToken(token, accessTokenExpirationSeconds);
  }

  @Override
  public String issueRefreshTokenFor(User user) {
    return issueTrackedToken(user, JwtTokenTypes.REFRESH_TOKEN, refreshTokenExpirationSeconds);
  }

  @Override
  public String issueConfirmationTokenFor(User user) {
    return issueTrackedToken(user, JwtTokenTypes.CONFIRMATION_TOKEN, confirmationTokenExpirationSeconds);
  }

  @Override
  public VerifiedConfirmationToken verify(String token) {
    try {
      Jwt jwt = jwtDecoder.decode(token);
      String tokenUse = jwt.getClaimAsString(JwtTokenClaims.TOKEN_USE.value());

      if (!JwtTokenTypes.CONFIRMATION_TOKEN.value().equals(tokenUse)) {
        throw new InvalidTokenException("Invalid token type for confirmation");
      }

      Token persistedToken = tokenRepository.findByToken(token)
          .orElseThrow(() -> new InvalidTokenException("Invalid token provided"));

      if (!jwt.getId().equals(persistedToken.getId().id().toString())) {
        throw new InvalidTokenException("Invalid token provided");
      }

      persistedToken.validateCanBeConsumed();
      persistedToken.markAsUsed();
      tokenRepository.save(persistedToken);

      return new VerifiedConfirmationToken(
          jwt.getSubject(),
          jwt.getClaimAsString(JwtTokenClaims.EMAIL.value())
      );
    } catch (InvalidTokenException ex) {
      throw ex;
    } catch (JwtException ex) {
      String message = ex.getMessage() == null ? "Invalid token" : ex.getMessage().toLowerCase();
      if (message.contains("expired")) {
        throw new TokenExpiredException("Token has expired");
      }
      throw new InvalidTokenException("Invalid token provided");
    }
  }

  private String issueTrackedToken(User user, JwtTokenTypes tokenType, long expirationSeconds) {
    tokenRepository.revokeActiveTokens(user.getId().id(), tokenType.value());

    TokenID tokenId = TokenID.generate();
    long issuedAtMillis = System.currentTimeMillis();
    long expirationTime = issuedAtMillis + (expirationSeconds * 1000);
    String tokenValue = encode(user, tokenType, expirationSeconds, tokenId.id().toString());

    Token token = new Token(
        tokenId,
        tokenValue,
        user.getId().id(),
        tokenType.value(),
        expirationTime,
        issuedAtMillis,
        issuedAtMillis,
        issuedAtMillis,
        null
    );
    tokenRepository.save(token);
    return tokenValue;
  }

  private String encode(User user, JwtTokenTypes tokenType, long expirationSeconds, String jwtId) {
    Instant issuedAt = Instant.now();
    Instant expiresAt = issuedAt.plusSeconds(expirationSeconds);

    JwtClaimsSet.Builder claimsBuilder = JwtClaimsSet.builder()
        .id(jwtId)
        .issuer(issuer)
        .subject(user.getId().id())
        .issuedAt(issuedAt)
        .expiresAt(expiresAt)
        .claim(JwtTokenClaims.TOKEN_USE.value(), tokenType.value())
        .claim(JwtTokenClaims.EMAIL.value(), user.getEmail().getValue())
        .claim(JwtTokenClaims.USERNAME.value(), user.getUsername().getValue());

    if (JwtTokenTypes.ACCESS_TOKEN.equals(tokenType)) {
      claimsBuilder.audience(Arrays.stream(accessTokenAudience.split(","))
          .map(String::trim)
          .filter(audience -> !audience.isBlank())
          .toList());
    }

    JwtClaimsSet claimsSet = claimsBuilder.build();

    JwsHeader jwsHeader = JwsHeader.with(SignatureAlgorithm.RS256).keyId("auth-key-id").build();
    return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claimsSet)).getTokenValue();
  }
}