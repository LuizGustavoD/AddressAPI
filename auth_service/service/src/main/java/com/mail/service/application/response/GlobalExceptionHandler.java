package com.mail.service.application.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.mail.service.domain.exceptions.ErrorSentMailException;
import com.mail.service.domain.exceptions.ExpiredTokenException;
import com.mail.service.domain.exceptions.InvalidAPIKeyException;
import com.mail.service.domain.exceptions.InvalidRequestAddreesException;
import com.mail.service.domain.exceptions.InvalidTokenException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler({
      InvalidTokenException.class,
      ExpiredTokenException.class,
      InvalidAPIKeyException.class
  })
  public ResponseEntity<ErrorPayload> handleUnauthorizedExceptions(RuntimeException ex, HttpServletRequest request) {
    return buildError(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
  }

  @ExceptionHandler(ErrorSentMailException.class)
  public ResponseEntity<ErrorPayload> handleGatewayExceptions(RuntimeException ex, HttpServletRequest request) {
    return buildError(HttpStatus.BAD_GATEWAY, ex.getMessage(), request);
  }

  @ExceptionHandler(InvalidRequestAddreesException.class)
  public ResponseEntity<ErrorPayload> handleBadRequestDomainExceptions(RuntimeException ex, HttpServletRequest request) {
    return buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
  }

  @ExceptionHandler({
      MethodArgumentNotValidException.class,
      BindException.class,
      ConstraintViolationException.class,
      MethodArgumentTypeMismatchException.class,
      HttpMessageNotReadableException.class,
      HttpMediaTypeNotSupportedException.class
  })
  public ResponseEntity<ErrorPayload> handleValidationExceptions(Exception ex, HttpServletRequest request) {
    String message = resolveValidationMessage(ex);
    return buildError(HttpStatus.BAD_REQUEST, message, request);
  }

  @ExceptionHandler({
      BadCredentialsException.class,
      AuthenticationCredentialsNotFoundException.class,
      OAuth2AuthenticationException.class,
      AuthenticationException.class
  })
  public ResponseEntity<ErrorPayload> handleSecurityExceptions(Exception ex, HttpServletRequest request) {
    return buildError(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorPayload> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
    return buildError(HttpStatus.FORBIDDEN, ex.getMessage(), request);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorPayload> handleGenericException(Exception ex, HttpServletRequest request) {
    return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error", request);
  }

  private ResponseEntity<ErrorPayload> buildError(HttpStatus status, String message, HttpServletRequest request) {
    return ResponseEntity.status(status).body(ErrorPayload.error(
        status.value(),
        message,
        request.getRequestURI()
    ));
  }

  private String resolveValidationMessage(Exception ex) {
    if (ex instanceof MethodArgumentNotValidException validationException) {
      return validationException.getBindingResult()
          .getFieldErrors()
          .stream()
          .findFirst()
          .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
          .orElse("Invalid request payload");
    }

    if (ex instanceof BindException bindException) {
      return bindException.getFieldErrors()
          .stream()
          .findFirst()
          .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
          .orElse("Invalid request payload");
    }

    if (ex instanceof ConstraintViolationException constraintViolationException) {
      return constraintViolationException.getConstraintViolations()
          .stream()
          .findFirst()
          .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
          .orElse("Invalid request data");
    }

    if (ex instanceof MethodArgumentTypeMismatchException typeMismatchException) {
      return "Invalid value for parameter '" + typeMismatchException.getName() + "'";
    }

    if (ex instanceof HttpMessageNotReadableException) {
      return "Request body is missing or malformed";
    }

    if (ex instanceof HttpMediaTypeNotSupportedException) {
      return "Unsupported content type";
    }

    return "Invalid request data";
  }
}
