package com.mail.service.infra.http.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.mail.service.application.usecase.ConfirmAccountUseCase;
import com.mail.service.application.dto.MailSendDTO;
import com.mail.service.application.dto.MailSendResponseDTO;
import com.mail.service.application.response.ResponsePayload;
import com.mail.service.application.usecase.DispatchMailUseCase;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RequestMapping("/mail")
@RestController
@Validated
public class MailController {

  private final DispatchMailUseCase dispatchMailUseCase;
  private final ConfirmAccountUseCase confirmAccountUseCase;

  public MailController(DispatchMailUseCase dispatchMailUseCase, ConfirmAccountUseCase confirmAccountUseCase) {
    this.dispatchMailUseCase = dispatchMailUseCase;
    this.confirmAccountUseCase = confirmAccountUseCase;
  }

  @PostMapping("/send")
  public ResponseEntity<ResponsePayload<MailSendResponseDTO>> send(
      @Valid @RequestBody MailSendDTO mailSendDTO,
      HttpServletRequest request) {

    MailSendResponseDTO response = dispatchMailUseCase.execute(mailSendDTO, request.getRemoteAddr());

    return ResponseEntity.ok(ResponsePayload.success(response, 200, "Email dispatched successfully"));
  }

  @GetMapping("/confirm")
  public ResponseEntity<ResponsePayload<Void>> confirm(@RequestParam("token") String token) {
    confirmAccountUseCase.execute(token);
    return ResponseEntity.ok(ResponsePayload.success(null, 200, "Account activated successfully"));
  }
}
