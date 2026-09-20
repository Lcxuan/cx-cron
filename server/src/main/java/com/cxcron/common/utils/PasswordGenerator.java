package com.cxcron.common.utils;

import java.security.SecureRandom;

public final class PasswordGenerator {

    private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String CHARACTERS = LETTERS + DIGITS;
    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordGenerator() {
    }

    public static String generate() {
        StringBuilder password = new StringBuilder(12);
        password.append(LETTERS.charAt(RANDOM.nextInt(LETTERS.length())));
        password.append(DIGITS.charAt(RANDOM.nextInt(DIGITS.length())));
        for (int i = 2; i < 12; i++) {
            password.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return password.toString();
    }
}
