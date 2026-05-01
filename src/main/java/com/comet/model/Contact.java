package com.comet.model;

import com.comet.model.enums.ContactStatus;
import java.time.Instant;

public record Contact(
    Long userId,
    Long contactId,
    ContactStatus status,
    Instant addedAt
) {}