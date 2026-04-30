package com.binarysystempatienttracker.patienttracker.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

/**
 * Encapsulates JWT creation and validation for access and refresh tokens
 * Uses an HMAC secret; ensure the configured secret is sufficient long(>= 256 bits for HS256)
 */

@Service
public class JwtService {
    private final Key key;
    private final long expirationMs;
    private final long refreshExpirationMs;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-minutes}") long expMinutes,
            @Value("${app.jwt.refresh-expiration-days}") long refreshDays) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationMs = expMinutes * 60_000L;
        this.refreshExpirationMs = refreshDays * 24L * 60L * 60L * 1000L;
    }

    /**
     * Issues a short-lived access token for API requests
     */
    public String generateAccessToken(String subject) {
        return Jwts.builder().setSubject(subject).setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key, SignatureAlgorithm.HS256).compact();
    }

    /**
     * Issues a longer-lived refresh token
     */
    public String generateRefreshToken(String subject) {
        return Jwts.builder().setSubject(subject).setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpirationMs))
                .signWith(key, SignatureAlgorithm.HS256).compact();
    }

    /**
     * Extracts the subject (username/email) from the token
     */
    public String extractSubject(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * Basic signature/expiry validation
     */
    public boolean isValid(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

}
