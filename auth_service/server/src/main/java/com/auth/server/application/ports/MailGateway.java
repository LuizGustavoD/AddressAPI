package com.auth.server.application.ports;

public interface MailGateway {

  void sendHtmlMail(String userId, String activationToken, String to, String subject, String body);
}