package com.mail.service.application.usecase;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.mail.service.application.dto.MailSendDTO;
import com.mail.service.application.dto.MailSendResponseDTO;
import com.mail.service.domain.exceptions.ErrorSentMailException;
import com.mail.service.domain.persistence.entities.Access;
import com.mail.service.domain.persistence.entities.MailSend;
import com.mail.service.domain.persistence.repository.AccessRepository;
import com.mail.service.domain.persistence.repository.MailRepository;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;

@Component
public class DispatchMailUseCase {

  private final AccessRepository accessRepository;
  private final MailRepository mailRepository;
  private final JavaMailSender mailSender;

  public DispatchMailUseCase(
      AccessRepository accessRepository,
      MailRepository mailRepository,
      JavaMailSender mailSender) {
    this.accessRepository = accessRepository;
    this.mailRepository = mailRepository;
    this.mailSender = mailSender;
  }

  @Transactional
  public MailSendResponseDTO execute(MailSendDTO request, String callerAddress) {
    Access accessAudit = accessRepository.save(
        Access.create(request.userId(), hashToken(request.activationToken()), callerAddress));

    try {
      sendHtmlMail(request.to(), request.subject(), request.body());
      MailSend mailAudit = mailRepository.save(
          MailSend.success(request.userId(), request.to(), request.subject(), request.body(), accessAudit));

      return new MailSendResponseDTO(
          accessAudit.getId(),
          mailAudit.getId(),
          request.userId(),
          mailAudit.getStatus(),
          Instant.now());
    } catch (MessagingException | MailException ex) {
      MailSend mailAudit = mailRepository.save(
          MailSend.failure(request.userId(), request.to(), request.subject(), request.body(), ex.getMessage(), accessAudit));

      throw new ErrorSentMailException(
          "Failed to send email for user " + request.userId() + " (auditId=" + mailAudit.getId() + ")");
    }
  }

  private void sendHtmlMail(String to, String subject, String body) throws MessagingException {
    var mimeMessage = mailSender.createMimeMessage();
    var helper = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.name());
    helper.setTo(to);
    helper.setSubject(subject);
    helper.setText(body, true);
    mailSender.send(mimeMessage);
  }

  private String hashToken(String token) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
      return bytesToHex(hash);
    } catch (NoSuchAlgorithmException ex) {
      throw new IllegalStateException("SHA-256 algorithm unavailable", ex);
    }
  }

  private String bytesToHex(byte[] bytes) {
    StringBuilder hexString = new StringBuilder();
    for (byte b : bytes) {
      String hex = Integer.toHexString(0xff & b);
      if (hex.length() == 1) {
        hexString.append('0');
      }
      hexString.append(hex);
    }
    return hexString.toString();
  }
}
