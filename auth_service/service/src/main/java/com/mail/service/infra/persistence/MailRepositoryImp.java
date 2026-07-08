package com.mail.service.infra.persistence;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.mail.service.domain.persistence.entities.MailSend;
import com.mail.service.domain.persistence.repository.MailRepository;

@Repository
public class MailRepositoryImp implements MailRepository {

  private final AccessJPARepository accessJPARepository;
  private final MailSendJPARepository mailSendJPARepository;

  public MailRepositoryImp(AccessJPARepository accessJPARepository, MailSendJPARepository mailSendJPARepository) {
    this.accessJPARepository = accessJPARepository;
    this.mailSendJPARepository = mailSendJPARepository;
  }

  @Override
  public MailSend save(MailSend mailSend) {
    AccessEntity accessEntity = accessJPARepository.findById(mailSend.getAccessId())
        .orElseThrow(() -> new IllegalStateException("Access audit not found for id: " + mailSend.getAccessId()));

    MailSendEntity entity = MailSendEntity.fromDomain(mailSend, accessEntity);
    return mailSendJPARepository.save(entity).toDomain();
  }

  @Override
  public List<MailSend> findByUserId(String userId) {
    return mailSendJPARepository.findByUserId(userId)
        .stream()
        .map(MailSendEntity::toDomain)
        .collect(Collectors.toList());
  }
}
