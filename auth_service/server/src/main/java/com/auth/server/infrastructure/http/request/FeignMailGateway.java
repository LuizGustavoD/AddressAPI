package com.auth.server.infrastructure.http.request;

import org.springframework.stereotype.Component;

import com.auth.server.application.dto.MailRequest;
import com.auth.server.application.ports.MailGateway;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FeignMailGateway implements MailGateway {

  private final MailServiceRequest mailServiceRequest;

  @Override
  public void sendHtmlMail(String userId, String activationToken, String to, String subject, String body) {
    mailServiceRequest.sendMail(new MailRequest(userId, activationToken, to, subject, body));
  }
}