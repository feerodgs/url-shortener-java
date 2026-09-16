package com.feerodogs.urlshortener.repository;

import com.feerodogs.urlshortener.domain.ShortenedUrl;

import java.util.Optional;

public interface UrlRepository {

    void save(ShortenedUrl shortenedUrl);

    Optional<ShortenedUrl> findByShortCode(String shortCode);

    void incrementClicks(String shortCode);
}