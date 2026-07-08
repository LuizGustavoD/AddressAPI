package com.address.service.infrastructure.security;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import com.address.service.domain.model.auth.UserAccess;
import com.address.service.domain.response.exceptions.securityExceptions.AuthenticatedUserNotFoundException;
import com.address.service.domain.response.exceptions.securityExceptions.InvalidAuthenticatedPrincipalException;
import com.address.service.domain.response.exceptions.securityExceptions.InvalidTokenSubjectException;
import com.address.service.domain.response.exceptions.securityExceptions.MissingTokenClaimException;
import com.address.service.domain.services.AuthenticatedUserContext;

@Component
public class SpringSecurityAuthenticatedUserContext implements AuthenticatedUserContext {

  @Override
  public UserAccess getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      throw new AuthenticatedUserNotFoundException("Authenticated user not found");
    }

    if (!(authentication.getPrincipal() instanceof Jwt jwt)) {
      throw new InvalidAuthenticatedPrincipalException("Invalid authenticated principal");
    }

    String subject = jwt.getSubject();
    if (subject == null || subject.isBlank()) {
      throw new MissingTokenClaimException("Token subject claim is missing");
    }

    UUID userId;
    try {
      userId = UUID.fromString(subject);
    } catch (IllegalArgumentException ex) {
      throw new InvalidTokenSubjectException("Token subject is not a valid UUID");
    }

    String userName = jwt.getClaimAsString("username");
    if (userName == null || userName.isBlank()) {
      userName = authentication.getName();
    }

    String email = jwt.getClaimAsString("email");
    if (email == null || email.isBlank()) {
      throw new MissingTokenClaimException("Token email claim is missing");
    }

    return new UserAccess(userId, userName, email);
  }
}