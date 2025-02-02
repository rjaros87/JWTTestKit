package io.github.rjaros87.jwttestkit.utils;

import io.micronaut.core.annotation.Introspected;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.UUID;

@Introspected
public class JWTUtils {
    private static final long DEFAULT_EXP_TIME_SECONDS = 3600;

    private JWTUtils() {}

    public static String generateKeyId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    public static String getPrivateKeyPem(PrivateKey privateKey) {
        return convertToPem("PRIVATE KEY", privateKey.getEncoded());
    }

    public static String getPublicKeyPem(PublicKey publicKey) {
        return convertToPem("PUBLIC KEY", publicKey.getEncoded());
    }

    public static long getDefaultExpTime() {
        return System.currentTimeMillis() / 1000 + DEFAULT_EXP_TIME_SECONDS;
    }

    public static long getDefaultIatTime() {
        return System.currentTimeMillis() / 1000;
    }

    private static String convertToPem(String type, byte[] encoded) {
        String base64Encoded = Base64.getEncoder().encodeToString(encoded);
        return "-----BEGIN " + type + "-----\n" +
                base64Encoded + "\n" +
                "-----END " + type + "-----";
    }
}
