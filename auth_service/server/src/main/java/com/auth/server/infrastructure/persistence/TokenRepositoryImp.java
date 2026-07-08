package com.auth.server.infrastructure.persistence;

import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.auth.server.domain.persistence.models.token.Token;
import com.auth.server.domain.persistence.repository.TokenRepository;
import com.auth.server.infrastructure.persistence.tokens.TokenEntity;
import com.auth.server.infrastructure.persistence.tokens.TokenJPARepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
@Transactional
public class TokenRepositoryImp implements TokenRepository {

  private final TokenJPARepository tokenJPARepository;

  @Override
  public void save(Token token) {
    tokenJPARepository.save(TokenEntity.fromDomain(token));
  }

  @Override
  public Optional<Token> findByToken(String token) {
    return tokenJPARepository.findByToken(token).map(TokenEntity::toDomain);
  }

  @Override
  public void revokeActiveTokens(String userId, String tokenUse) {
    tokenJPARepository.deleteByUserIdAndTokenUseAndUsedAtIsNull(userId, tokenUse);
  }

  @Override
  public boolean existsByToken(String token) {
    return tokenJPARepository.existsByToken(token);
  }
}