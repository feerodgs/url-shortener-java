package com.feerodogs.urlshortener.service;

import com.feerodogs.urlshortener.domain.ShortenedUrl;
import com.feerodogs.urlshortener.dto.request.CreateShortUrlRequest;
import com.feerodogs.urlshortener.dto.response.CreateShortUrlResponse;
import com.feerodogs.urlshortener.dto.response.UrlStatisticsResponse;
import com.feerodogs.urlshortener.util.ShortCodeGenerator;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UrlShortenerService {

    private final Map<String, ShortenedUrl> urlsByShortCode = new ConcurrentHashMap<>();
    private final ShortCodeGenerator shortCodeGenerator;

    public UrlShortenerService(ShortCodeGenerator shortCodeGenerator) {
        this.shortCodeGenerator = shortCodeGenerator;
    }

    public CreateShortUrlResponse create(CreateShortUrlRequest request, String baseUrl) {
        String shortCode = generateUniqueShortCode();
        Instant createdAt = Instant.now();

        ShortenedUrl shortenedUrl = new ShortenedUrl(shortCode, request.url(), createdAt);

        urlsByShortCode.put(shortCode, shortenedUrl);

        return new CreateShortUrlResponse(shortCode, baseUrl + "/" + shortCode, shortenedUrl.getOriginalUrl(), shortenedUrl.getCreatedAt());
    }

    public Optional<ShortenedUrl> findByShortCode(String shortCode) {
        return Optional.ofNullable(urlsByShortCode.get(shortCode));
    }

    public Optional<UrlStatisticsResponse> getStatistics(String shortCode) {
        return findByShortCode(shortCode).map(shortenedUrl -> new UrlStatisticsResponse(shortenedUrl.getShortCode(), shortenedUrl.getOriginalUrl(), shortenedUrl.getCreatedAt(), shortenedUrl.getClicks()));
    }

    public void registerClick(ShortenedUrl shortenedUrl) {
        shortenedUrl.incrementClicks();
    }

    private String generateUniqueShortCode() {
        String shortCode;

        do {
            shortCode = shortCodeGenerator.generate();
        } while (urlsByShortCode.containsKey(shortCode));

        return shortCode;
    }
}