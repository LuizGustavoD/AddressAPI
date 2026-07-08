package com.auth.server.application.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.auth.server.infrastructure.security.MailServiceAccessTokenProvider;

import feign.RequestInterceptor;

@Configuration
public class FeignClientConfig {
  
  private final MailServiceAccessTokenProvider mailServiceAccessTokenProvider;

  public FeignClientConfig(MailServiceAccessTokenProvider mailServiceAccessTokenProvider) {
    this.mailServiceAccessTokenProvider = mailServiceAccessTokenProvider;
  }

  @Bean
  public RequestInterceptor requestInterceptor() {
    return requestTemplate -> requestTemplate.header(
        "Authorization",
        "Bearer " + mailServiceAccessTokenProvider.generateToken());
  }
}
