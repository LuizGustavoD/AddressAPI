package com.auth.server.application.response;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.validation.ConstraintViolationException;

import com.auth.server.domain.response.exceptions.authExceptions.AccountLockedException;
import com.auth.server.domain.response.exceptions.authExceptions.InvalidCredentialsException;
import com.auth.server.domain.response.exceptions.authExceptions.UserAlreadyExistException;
import com.auth.server.domain.response.exceptions.authExceptions.UserNotActiveException;
import com.auth.server.domain.response.exceptions.authExceptions.UserNotFoundException;
import com.auth.server.domain.response.exceptions.authorizationExceptions.InsufficientScopeException;
import com.auth.server.domain.response.exceptions.authorizationExceptions.InvalidClientException;
import com.auth.server.domain.response.exceptions.authorizationExceptions.InvalidGrantException;
import com.auth.server.domain.response.exceptions.authorizationExceptions.InvalidTokenException;
import com.auth.server.domain.response.exceptions.authorizationExceptions.RefreshTokenExpiredException;
import com.auth.server.domain.response.exceptions.authorizationExceptions.SignatureException;
import com.auth.server.domain.response.exceptions.authorizationExceptions.TokenExpiredException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler({
      UserNotFoundException.class,
      UserAlreadyExistException.class,
      UserNotActiveException.class,
      InvalidCredentialsException.class,
      AccountLockedException.class,
      InvalidClientException.class,
      InvalidGrantException.class,
      InvalidTokenException.class,
      RefreshTokenExpiredException.class,
      SignatureException.class,
      TokenExpiredException.class,
      InsufficientScopeException.class
  })
  public ResponseEntity<ErrorPayload> handleAuthExceptions(RuntimeException ex, HttpServletRequest request) {
    HttpStatus status = resolveStatus(ex);
    return buildError(status, ex.getMessage(), request);
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
    ex.printStackTrace();
    return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error", request);
  }

  private ResponseEntity<ErrorPayload> buildError(HttpStatus status, String message, HttpServletRequest request) {
    return ResponseEntity.status(status).body(ErrorPayload.error(
        status.value(),
        message,
        request.getRequestURI()
    ));
  }

  private HttpStatus resolveStatus(RuntimeException ex) {
    if (ex instanceof UserNotFoundException) {
      return HttpStatus.NOT_FOUND;
    }

    if (ex instanceof UserAlreadyExistException) {
      return HttpStatus.CONFLICT;
    }

    if (ex instanceof InsufficientScopeException) {
      return HttpStatus.FORBIDDEN;
    }

    return HttpStatus.UNAUTHORIZED;
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
