package com.feerodogs.urlshortener.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class ShortCodeGenerator {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private static final int SHORT_CODE_LENGTH = 6;

    private final SecureRandom random = new SecureRandom();

    public String generate() {
        StringBuilder shortCode = new StringBuilder(SHORT_CODE_LENGTH);

        for (int index = 0; index < SHORT_CODE_LENGTH; index++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            shortCode.append(CHARACTERS.charAt(randomIndex));
        }

        return shortCode.toString();
    }
}