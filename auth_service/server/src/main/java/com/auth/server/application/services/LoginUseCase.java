package com.auth.server.application.services;

import org.springframework.stereotype.Service;

import com.auth.server.application.dto.AuthResponse;
import com.auth.server.application.dto.LoginCommand;
import com.auth.server.application.ports.AccessTokenIssuer;
import com.auth.server.application.ports.IssuedAccessToken;
import com.auth.server.application.ports.PasswordHasher;
import com.auth.server.application.ports.RefreshTokenIssuer;
import com.auth.server.domain.persistence.models.user.User;
import com.auth.server.domain.persistence.repository.UserRepository;
import com.auth.server.domain.response.exceptions.authExceptions.InvalidCredentialsException;
import com.auth.server.domain.response.exceptions.authExceptions.UserNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginUseCase {

  private final UserRepository userRepository;
  private final PasswordHasher passwordHasher;
  private final AccessTokenIssuer accessTokenIssuer;
  private final RefreshTokenIssuer refreshTokenIssuer;

  public AuthResponse execute(LoginCommand command) {
    try {
      User user = userRepository.findByUsername(command.username());

      user.validateIsActive();

      if (!passwordHasher.matches(command.password(), user.getPassword().getValue())) {
        throw new InvalidCredentialsException("Invalid username or password");
      }

      IssuedAccessToken accessToken = accessTokenIssuer.issueAccessTokenFor(user);
      String refreshToken = refreshTokenIssuer.issueRefreshTokenFor(user);

      return new AuthResponse(
          accessToken.token(),
          refreshToken,
          accessToken.expiresIn()
      );
    } catch (UserNotFoundException ex) {
      throw new InvalidCredentialsException("Invalid username or password");
    }
  }

}

