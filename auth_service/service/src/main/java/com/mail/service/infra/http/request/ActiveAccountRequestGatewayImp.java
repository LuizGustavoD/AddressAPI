package com.mail.service.infra.http.request;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mail.service.application.ports.ActiveAccountRequestGateway;

@Component
public class ActiveAccountRequestGatewayImp implements ActiveAccountRequestGateway {

  private final AuthServerRequest authServerRequest;
  private final String apiKey;

  public ActiveAccountRequestGatewayImp(
      AuthServerRequest authServerRequest,
      @Value("${auth-server.api-key}") String apiKey) {
    this.authServerRequest = authServerRequest;
    this.apiKey = apiKey;
  }

  @Override
  public void sendActiveAccountRequest(String token) {
    authServerRequest.activateAccount(apiKey, token);
  }
}
