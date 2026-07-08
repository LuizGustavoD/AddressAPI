package com.address.service.domain.model.auth;

import java.util.UUID;

public record UserAccess(
    UUID userId,
    String userName,
    String email) {
}
