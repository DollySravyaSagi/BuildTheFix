package com.buildthefix.app.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * Utility class for hashing and verifying user passwords safely.
 */
public class PasswordEncoderUtils {

    public static String hashPassword(String rawPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    public static boolean matches(String rawPassword, String hashedPassword) {
        String inputHash = hashPassword(rawPassword);
        return inputHash.equalsIgnoreCase(hashedPassword);
    }
}
