package com.auth.server.infrastructure.persistence.tokens;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenJPARepository extends JpaRepository<TokenEntity, String> {

  Optional<TokenEntity> findByToken(String token);

  void deleteByUserIdAndTokenUseAndUsedAtIsNull(String userId, String tokenUse);

  boolean existsByToken(String token);
}