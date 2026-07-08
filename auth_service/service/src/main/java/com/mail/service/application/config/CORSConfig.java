package com.mail.service.application.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class CORSConfig implements org.springframework.web.servlet.config.annotation.WebMvcConfigurer {

  @Override
  public void addCorsMappings(org.springframework.web.servlet.config.annotation.CorsRegistry registry) {
    registry.addMapping("/**")
            .allowedOrigins("http://localhost:3000", "http://localhost:8080")
            .allowedMethods("POST")
            .allowedHeaders("*");
  }
  
}
