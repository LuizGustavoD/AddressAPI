package com.auth.server.infrastructure.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

@Component
public class JwtKeyProcessors {

  private final Resource privateKeyResource;
  private final Resource publicKeyResource;

  public JwtKeyProcessors(
      @Value("${jwt.private-key-path:classpath:keys/private/private-key.pem}") Resource privateKeyResource,
      @Value("${jwt.public-key-path:classpath:keys/public/public-key.pem}") Resource publicKeyResource) {
    this.privateKeyResource = privateKeyResource;
    this.publicKeyResource = publicKeyResource;
  }

  public RSAPublicKey getProcessPublicKey() {
    try {
      byte[] keyBytes = decodePem(readResource(publicKeyResource), "-----BEGIN PUBLIC KEY-----", "-----END PUBLIC KEY-----");
      X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
      return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(keySpec);
    } catch (IOException | GeneralSecurityException ex) {
      throw new IllegalStateException("Failed to load JWT public key", ex);
    }
  }

  public RSAPrivateKey getProcessPrivateKey() {
    try {
      byte[] keyBytes = decodePem(readResource(privateKeyResource), "-----BEGIN PRIVATE KEY-----", "-----END PRIVATE KEY-----");
      PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
      return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(keySpec);
    } catch (IOException | GeneralSecurityException ex) {
      throw new IllegalStateException("Failed to load JWT private key", ex);
    }
  }

  private String readResource(Resource resource) throws IOException {
    return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
  }

  private byte[] decodePem(String content, String beginMarker, String endMarker) {
    String normalized = content
        .replace(beginMarker, "")
        .replace(endMarker, "")
        .replaceAll("\\s", "");
    return Base64.getDecoder().decode(normalized);
  }
}