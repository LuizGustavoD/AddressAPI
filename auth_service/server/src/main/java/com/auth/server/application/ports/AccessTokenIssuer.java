package com.auth.server.application.ports;

import com.auth.server.domain.persistence.models.user.User;

public interface AccessTokenIssuer {
  IssuedAccessToken issueAccessTokenFor(User user);
}
