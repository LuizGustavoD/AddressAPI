package com.mail.service.infra.http.request;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "auth-server",
    url = "${auth-server.url}"
)
public interface AuthServerRequest {

  @PostMapping("/api/auth/activate")
  void activateAccount(
      @RequestHeader("X-API-KEY") String apiKey,
      @RequestParam("token") String token
  );
}
