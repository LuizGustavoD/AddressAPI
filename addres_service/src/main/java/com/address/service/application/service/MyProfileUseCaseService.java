package com.address.service.application.service;

import com.address.service.domain.model.auth.UserAccess;
import com.address.service.domain.services.AuthenticatedUserContext;
import com.address.service.domain.services.MyProfileUseCase;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyProfileUseCaseService implements MyProfileUseCase {

  private final AuthenticatedUserContext authenticatedUserContext;

  @Override
  public UserAccess getMyProfile() {
    return authenticatedUserContext.getCurrentUser();
  }

}
