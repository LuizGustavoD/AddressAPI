package com.auth.server.application.services;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import com.auth.server.application.ports.ConfirmationTokenIssuer;
import com.auth.server.application.ports.MailGateway;
import com.auth.server.domain.persistence.models.user.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SendConfirmationMailUseCase {

	private static final String USERNAME_PLACEHOLDER = "{{username}}";
	private static final String CONFIRMATION_LINK_PLACEHOLDER = "{{confirmation_link}}";

	private final MailGateway mailGateway;
	private final ConfirmationTokenIssuer confirmationTokenIssuer;

	@Value("classpath:templates/mailBody/mailConfirmMessage.html")
	private Resource confirmationMailTemplate;

	@Value("${app.mail.confirmation-subject:Confirme seu cadastro}")
	private String confirmationSubject;

	@Value("${app.mail.confirmation-url-template:http://localhost:8080/api/auth/confirm?token={token}}")
	private String confirmationUrlTemplate;

	public void execute(User user) {
		String confirmationToken = confirmationTokenIssuer.issueConfirmationTokenFor(user);
		String htmlBody = buildHtmlBody(user, confirmationToken);

		mailGateway.sendHtmlMail(
				user.getId().id(),
				confirmationToken,
				user.getEmail().getValue(),
				confirmationSubject,
				htmlBody);
	}

	private String buildHtmlBody(User user, String confirmationToken) {
		return loadTemplate()
				.replace(USERNAME_PLACEHOLDER, user.getUsername().getValue())
				.replace(CONFIRMATION_LINK_PLACEHOLDER, buildConfirmationLink(confirmationToken));
	}

	private String buildConfirmationLink(String confirmationToken) {
		return confirmationUrlTemplate
				.replace("{token}", confirmationToken);
	}

	private String loadTemplate() {
		try (InputStream inputStream = confirmationMailTemplate.getInputStream()) {
			return StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
		} catch (IOException ex) {
			throw new IllegalStateException("Failed to load confirmation mail template", ex);
		}
	}
}
