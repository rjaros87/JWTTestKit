package io.github.rjaros87.jwttestkit.utils;

import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

public class Faker {

    private Faker() {
        throw new IllegalStateException("Utility class");
    }

    private static final String[] WORDS = {
            "lorem", "ipsum", "dolor", "sit", "amet", "consectetur", "adipiscing", "elit"
    };

    private static final Random RANDOM = new SecureRandom();

    public static String randomWord() {
        return WORDS[RANDOM.nextInt(WORDS.length)];
    }

    public static String randomUrl() {
        return "http://example.com/" + UUID.randomUUID().toString();
    }

    public static int randomDigitNotZero() {
        return RANDOM.nextInt(9) + 1; // Random number between 1 and 9
    }

    public static String randomText(int length) {
        StringBuilder text = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            text.append((char) ('a' + RANDOM.nextInt(26)));
        }
        return text.toString();
    }

    public static String randomEmailAddress() {
        String username = randomText(5);
        String domain = randomText(5);
        return username + "@" + domain + ".com";
    }
}