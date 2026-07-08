package com.address.service.domain.services;

import com.address.service.domain.model.auth.UserAccess;

public interface AuthenticatedUserContext {

  UserAccess getCurrentUser();
}