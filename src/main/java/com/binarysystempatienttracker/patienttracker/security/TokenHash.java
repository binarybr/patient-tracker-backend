package com.binarysystempatienttracker.patienttracker.security;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Utility to hash sensitive tokens using SHA-256 and encode as Base64
 * Storing only hashes helps prevent credential leakage if the DB is compromised
 */
@Component
public class TokenHash {
    /**
     * Returns Base64(SHA-256(token))
     */
    public String hash(String token) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
