package com.auth.server.domain.persistence.repository;

import java.util.Optional;

import com.auth.server.domain.persistence.models.token.Token;

public interface TokenRepository {

  void save(Token token);

  Optional<Token> findByToken(String token);

  void revokeActiveTokens(String userId, String tokenUse);

  boolean existsByToken(String token);
}