package com.comet.model;

import java.time.Instant;

public record Message(
    Long id,
    Long chatId,
    Long senderId,
    String content,
    boolean isDeleted,
    Instant updatedAt,
    Instant createdAt
) {}