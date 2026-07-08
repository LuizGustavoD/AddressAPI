package com.mail.service.infra.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MailSendJPARepository extends JpaRepository<MailSendEntity, UUID> {

  List<MailSendEntity> findByUserId(String userId);
}
