package com.auth.server.application.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auth.server.application.dto.RegisterCommand;
import com.auth.server.application.dto.UserResponse;
import com.auth.server.application.ports.PasswordHasher;
import com.auth.server.domain.persistence.models.user.Email;
import com.auth.server.domain.persistence.models.user.PasswordHash;
import com.auth.server.domain.persistence.models.user.User;
import com.auth.server.domain.persistence.models.user.UserID;
import com.auth.server.domain.persistence.models.user.Username;
import com.auth.server.domain.persistence.repository.UserRepository;
import com.auth.server.domain.response.exceptions.authExceptions.InvalidCredentialsException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegisterUseCase {

  private final UserRepository userRepository;
  private final PasswordHasher passwordHasher;
  private final SendConfirmationMailUseCase sendConfirmationMailUseCase;

  @Transactional
  public UserResponse execute(RegisterCommand command) {
    if (!command.password().equals(command.confirmPassword())) {
      throw new InvalidCredentialsException("Invalid credentials provided");
    }

    Username username = new Username(command.username());
    Email email = new Email(command.email());
    String encodedPassword = passwordHasher.encode(command.password());
    PasswordHash passwordHash = new PasswordHash(encodedPassword);

    User user = new User(
        UserID.generate(),
        username,
        passwordHash,
        email
    );

    userRepository.save(user);
    sendConfirmationMailUseCase.execute(user);

    return new UserResponse(
        user.getId().id(),
        user.getUsername().getValue(),
        user.getEmail().getValue(),
        user.isActive()
    );
  }

}
