package com.comet.network.payload;

public record ChatMessagePayload(
    Long chatId,
    Long senderId,
    String content
) {}