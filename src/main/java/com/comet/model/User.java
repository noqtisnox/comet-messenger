package com.comet.model;

import java.time.Instant;

public record User(
    Long id,
    String username,
    String passwordHash,
    Instant lastActiveAt,
    Instant createdAt
) {}
