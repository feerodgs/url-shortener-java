package com.feerodogs.urlshortener.dto.response;

import java.time.Instant;

public record CreateShortUrlResponse(
        String shortCode,
        String shortUrl,
        String originalUrl,
        Instant createdAt
) {
}