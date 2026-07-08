package com.mail.service.domain.persistence.repository;

import java.util.List;

import com.mail.service.domain.persistence.entities.MailSend;

public interface MailRepository {

  MailSend save(MailSend mailSend);

  List<MailSend> findByUserId(String userId);
}
