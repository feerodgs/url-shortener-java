package com.feerodogs.urlshortener.domain;

import java.time.Instant;

public class ShortenedUrl {

    private final String shortCode;
    private final String originalUrl;
    private final Instant createdAt;
    private long clicks;

    public ShortenedUrl(String shortCode, String originalUrl, Instant createdAt) {
        this.shortCode = shortCode;
        this.originalUrl = originalUrl;
        this.createdAt = createdAt;
        this.clicks = 0;
    }

    public String getShortCode() {
        return shortCode;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public long getClicks() {
        return clicks;
    }

    public void incrementClicks() {
        this.clicks++;
    }
}