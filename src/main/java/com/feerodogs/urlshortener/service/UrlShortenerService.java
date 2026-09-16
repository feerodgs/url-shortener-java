package com.feerodogs.urlshortener.service;

import com.feerodogs.urlshortener.domain.ShortenedUrl;
import com.feerodogs.urlshortener.dto.request.CreateShortUrlRequest;
import com.feerodogs.urlshortener.dto.response.CreateShortUrlResponse;
import com.feerodogs.urlshortener.dto.response.UrlStatisticsResponse;
import com.feerodogs.urlshortener.repository.UrlRepository;
import com.feerodogs.urlshortener.util.ShortCodeGenerator;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class UrlShortenerService {

    private final UrlRepository urlRepository;
    private final ShortCodeGenerator shortCodeGenerator;

    public UrlShortenerService(
            UrlRepository urlRepository,
            ShortCodeGenerator shortCodeGenerator
    ) {
        this.urlRepository = urlRepository;
        this.shortCodeGenerator = shortCodeGenerator;
    }

    public CreateShortUrlResponse create(CreateShortUrlRequest request, String baseUrl) {
        String shortCode = generateUniqueShortCode();
        Instant createdAt = Instant.now();

        ShortenedUrl shortenedUrl = new ShortenedUrl(
                shortCode,
                request.url(),
                createdAt
        );

        urlRepository.save(shortenedUrl);

        return new CreateShortUrlResponse(
                shortCode,
                baseUrl + "/" + shortCode,
                shortenedUrl.getOriginalUrl(),
                Instant.parse(shortenedUrl.getCreatedAt())
        );
    }

    public Optional<ShortenedUrl> findByShortCode(String shortCode) {
        return urlRepository.findByShortCode(shortCode);
    }

    public Optional<UrlStatisticsResponse> getStatistics(String shortCode) {
        return findByShortCode(shortCode)
                .map(shortenedUrl -> new UrlStatisticsResponse(
                        shortenedUrl.getShortCode(),
                        shortenedUrl.getOriginalUrl(),
                        Instant.parse(shortenedUrl.getCreatedAt()),
                        shortenedUrl.getClicks()
                ));
    }

    public void registerClick(String shortCode) {
        urlRepository.incrementClicks(shortCode);
    }

    private String generateUniqueShortCode() {
        String shortCode;

        do {
            shortCode = shortCodeGenerator.generate();
        } while (urlRepository.findByShortCode(shortCode).isPresent());

        return shortCode;
    }
}