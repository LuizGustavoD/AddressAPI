package com.auth.server.infrastructure.http.request;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.auth.server.application.config.FeignClientConfig;
import com.auth.server.application.dto.MailRequest;

@FeignClient(
        name = "mail-service",
        url = "${mail-service.url}",
        configuration = FeignClientConfig.class 
)
public interface MailServiceRequest {
  
  @PostMapping("/mail/send")
  void sendMail(@RequestBody MailRequest mailRequest);
  
}
