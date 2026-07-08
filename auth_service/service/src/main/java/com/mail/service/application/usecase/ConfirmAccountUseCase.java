package com.mail.service.application.usecase;

import org.springframework.stereotype.Component;

import com.mail.service.application.ports.ActiveAccountRequestGateway;

@Component
public class ConfirmAccountUseCase {

  private final ActiveAccountRequestGateway activeAccountRequestGateway;

  public ConfirmAccountUseCase(ActiveAccountRequestGateway activeAccountRequestGateway) {
    this.activeAccountRequestGateway = activeAccountRequestGateway;
  }

  public void execute(String token) {
    if (token == null || token.trim().isEmpty()) {
      throw new IllegalArgumentException("Token cannot be empty");
    }
    activeAccountRequestGateway.sendActiveAccountRequest(token);
  }
}
