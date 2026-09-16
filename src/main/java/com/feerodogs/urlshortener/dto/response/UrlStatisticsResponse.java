package com.feerodogs.urlshortener.dto.response;

import java.time.Instant;

public record UrlStatisticsResponse(
        String shortCode,
        String originalUrl,
        Instant createdAt,
        long clicks
) {
}