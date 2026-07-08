package com.auth.server.application.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auth.server.application.dto.UserResponse;
import com.auth.server.application.ports.ConfirmationTokenVerifier;
import com.auth.server.application.ports.VerifiedConfirmationToken;
import com.auth.server.domain.persistence.models.user.User;
import com.auth.server.domain.persistence.models.user.UserID;
import com.auth.server.domain.persistence.repository.UserRepository;
import com.auth.server.domain.response.exceptions.authorizationExceptions.InvalidTokenException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConfirmRegistrationUseCase {

  private final UserRepository userRepository;
  private final ConfirmationTokenVerifier confirmationTokenVerifier;

  @Transactional
  public UserResponse execute(String token) {
    VerifiedConfirmationToken verifiedConfirmationToken = confirmationTokenVerifier.verify(token);
    User user = userRepository.findById(UserID.of(verifiedConfirmationToken.userId()));

    if (!user.getEmail().getValue().equals(verifiedConfirmationToken.email())) {
      throw new InvalidTokenException("Token does not match the target user");
    }

    user.activate();
    userRepository.update(user);

    return new UserResponse(
        user.getId().id(),
        user.getUsername().getValue(),
        user.getEmail().getValue(),
        user.isActive()
    );
  }
}