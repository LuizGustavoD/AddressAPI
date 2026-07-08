package com.auth.server.application.response;

import java.time.Instant;

public record ResponsePayload<T>(
    int status,
    boolean success,
    T data,
    String message,
    Instant timestamp
) {
    
    public static <T> ResponsePayload<T> success(T data, int statusCode) {
        return new ResponsePayload<>(
            statusCode,
            true,
            data,
            null,
            Instant.now()
        );
    }
    
    public static <T> ResponsePayload<T> success(T data, int statusCode, String message) {
        return new ResponsePayload<>(
            statusCode,
            true,
            data,
            message,
            Instant.now()
        );
    }
}

