package com.feerodogs.urlshortener.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateShortUrlRequest(
        @NotBlank(message = "A URL é obrigatória") @Pattern(regexp = "^(https?://).+$", message = "A URL deve começar com http:// ou https://") String url) {
}