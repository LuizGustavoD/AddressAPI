package com.mail.service.application.response;

import java.time.Instant;

public record ErrorPayload(
    int status,
    boolean success,
    String message,
    String path,
    Instant timestamp
) {

    public static ErrorPayload error(int statusCode, String message, String path) {
        return new ErrorPayload(
            statusCode,
            false,
            message,
            path,
            Instant.now()
        );
    }
}

