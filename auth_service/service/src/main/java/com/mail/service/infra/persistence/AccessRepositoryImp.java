package com.mail.service.infra.persistence;

import org.springframework.stereotype.Repository;

import com.mail.service.domain.persistence.entities.Access;
import com.mail.service.domain.persistence.repository.AccessRepository;

@Repository
public class AccessRepositoryImp implements AccessRepository {

  private final AccessJPARepository accessJPARepository;

  public AccessRepositoryImp(AccessJPARepository accessJPARepository) {
    this.accessJPARepository = accessJPARepository;
  }

  @Override
  public Access save(Access access) {
    AccessEntity entity = AccessEntity.fromDomain(access);
    return accessJPARepository.save(entity).toDomain();
  }
}
