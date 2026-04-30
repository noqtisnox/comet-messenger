package com.comet.model;

import java.time.Instant;

public record Chat(
    Long id,
    boolean isGroupChat,
    String groupName,
    Instant createdAt
) {}