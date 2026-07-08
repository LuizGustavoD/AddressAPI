package com.mail.service.application.ports;

public interface ActiveAccountRequestGateway {
  
  void sendActiveAccountRequest(String token);
}
