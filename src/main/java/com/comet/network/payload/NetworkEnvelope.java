package com.comet.network.payload;

import com.fasterxml.jackson.databind.JsonNode;

public record NetworkEnvelope(
        String type,
        JsonNode payload
) {}