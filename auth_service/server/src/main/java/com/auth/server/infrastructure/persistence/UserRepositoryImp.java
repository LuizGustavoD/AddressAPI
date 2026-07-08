package com.auth.server.infrastructure.persistence;

import org.springframework.stereotype.Repository;

import com.auth.server.domain.persistence.models.user.User;
import com.auth.server.domain.persistence.models.user.UserID;
import com.auth.server.domain.persistence.repository.UserRepository;
import com.auth.server.domain.response.exceptions.authExceptions.UserAlreadyExistException;
import com.auth.server.domain.response.exceptions.authExceptions.UserNotFoundException;
import com.auth.server.infrastructure.persistence.user.UserEntity;
import com.auth.server.infrastructure.persistence.user.UserJPARepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImp implements UserRepository {

  private final UserJPARepository userJPARepository;

  @Override
  public void save(User user) {
    if (userJPARepository.existsById(user.getId().id())) {
      throw new UserAlreadyExistException("User already exists");
    }

    if (userJPARepository.existsByUsername(user.getUsername().getValue())) {
      throw new UserAlreadyExistException("Username already in use");
    }

    if (userJPARepository.existsByEmail(user.getEmail().getValue())) {
      throw new UserAlreadyExistException("Email already in use");
    }

    userJPARepository.save(UserEntity.fromDomain(user));
  }

  @Override
  public User findById(UserID id) {
    return userJPARepository.findById(id.id())
        .map(UserEntity::toDomain)
        .orElseThrow(() -> new UserNotFoundException("User not found"));
  }

  @Override
  public User findByUsername(String username) {
    return userJPARepository.findByUsername(username)
        .map(UserEntity::toDomain)
        .orElseThrow(() -> new UserNotFoundException("User not found"));
  }

  @Override
  public User findByEmail(String email) {
    return userJPARepository.findByEmail(email)
        .map(UserEntity::toDomain)
        .orElseThrow(() -> new UserNotFoundException("User not found"));
  }

  @Override
  public void update(User user) {
    if (!userJPARepository.existsById(user.getId().id())) {
      throw new UserNotFoundException("User not found");
    }

    userJPARepository.findByUsername(user.getUsername().getValue())
        .filter(existingUser -> !existingUser.getId().equals(user.getId().id()))
        .ifPresent(existingUser -> {
          throw new UserAlreadyExistException("Username already in use");
        });

    userJPARepository.findByEmail(user.getEmail().getValue())
        .filter(existingUser -> !existingUser.getId().equals(user.getId().id()))
        .ifPresent(existingUser -> {
          throw new UserAlreadyExistException("Email already in use");
        });

    userJPARepository.save(UserEntity.fromDomain(user));
  }

  @Override
  public void delete(UserID id) {
    if (!userJPARepository.existsById(id.id())) {
      throw new UserNotFoundException("User not found");
    }

    userJPARepository.deleteById(id.id());
  }

  @Override
  public boolean existsById(UserID id) {
    return userJPARepository.existsById(id.id());
  }

  @Override
  public boolean existsByUsername(String username) {
    return userJPARepository.existsByUsername(username);
  }

  @Override
  public boolean existsByEmail(String email) {
    return userJPARepository.existsByEmail(email);
  }
}