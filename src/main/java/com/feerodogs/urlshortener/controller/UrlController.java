package com.feerodogs.urlshortener.controller;

import com.feerodogs.urlshortener.domain.ShortenedUrl;
import com.feerodogs.urlshortener.dto.request.CreateShortUrlRequest;
import com.feerodogs.urlshortener.dto.response.CreateShortUrlResponse;
import com.feerodogs.urlshortener.dto.response.UrlStatisticsResponse;
import com.feerodogs.urlshortener.service.UrlShortenerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UrlController {

    private final UrlShortenerService urlShortenerService;

    public UrlController(UrlShortenerService urlShortenerService) {
        this.urlShortenerService = urlShortenerService;
    }

    @PostMapping("/api/urls")
    public ResponseEntity<CreateShortUrlResponse> createShortUrl(@Valid @RequestBody CreateShortUrlRequest request, HttpServletRequest httpServletRequest) {
        String baseUrl = getBaseUrl(httpServletRequest);

        CreateShortUrlResponse response = urlShortenerService.create(request, baseUrl);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/api/urls/{shortCode}")
    public ResponseEntity<UrlStatisticsResponse> getUrlStatistics(@PathVariable String shortCode) {
        return urlShortenerService.getStatistics(shortCode).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String shortCode) {
        return urlShortenerService.findByShortCode(shortCode).map(this::createRedirectResponse).orElseGet(() -> ResponseEntity.notFound().build());
    }

    private ResponseEntity<Void> createRedirectResponse(ShortenedUrl shortenedUrl) {
        urlShortenerService.registerClick(shortenedUrl);

        return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, shortenedUrl.getOriginalUrl()).build();
    }

    private String getBaseUrl(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
    }
}