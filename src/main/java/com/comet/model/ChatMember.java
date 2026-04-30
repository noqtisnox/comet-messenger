package com.comet.model;

import com.comet.model.enums.ChatRole;
import java.time.Instant;

public record ChatMember(
    Long chatId,
    Long userId,
    ChatRole role,
    Instant lastReadAt,
    Instant joinedAt
) {}