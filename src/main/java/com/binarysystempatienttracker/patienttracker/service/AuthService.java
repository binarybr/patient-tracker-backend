package com.binarysystempatienttracker.patienttracker.service;

import com.binarysystempatienttracker.patienttracker.domain.RefreshToken;
import com.binarysystempatienttracker.patienttracker.domain.UserAccount;
import com.binarysystempatienttracker.patienttracker.repository.RefreshTokenRepository;
import com.binarysystempatienttracker.patienttracker.repository.UserRepository;
import com.binarysystempatienttracker.patienttracker.security.JwtService;
import com.binarysystempatienttracker.patienttracker.security.TokenHash;
import com.binarysystempatienttracker.patienttracker.web.dto.AuthResponseDto;
import com.binarysystempatienttracker.patienttracker.web.dto.LoginRequestDto;
import com.binarysystempatienttracker.patienttracker.web.dto.RegisterRequestDto;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Authentication/Authorization use-cases: register, login, refresh
 * Stores/rotates hashed refreshed tokens to support revocation
 */

@AllArgsConstructor
@Service
public class AuthService {
    private final AuthenticationManager am;
    private final UserRepository users;
    private final PasswordEncoder pe;
    private final JwtService jwt;
    private final RefreshTokenRepository refreshRepo;
    private final TokenHash tokenHash;

    /**
     * Issues both access refresh tokens and persists the latter(hashed)
     */
    private AuthResponseDto issueTokens(String subject) {
        String access = jwt.generateAccessToken(subject);
        String refresh = jwt.generateRefreshToken(subject);
        RefreshToken rt = RefreshToken.builder()
                .subject(subject)
                .tokenHash(tokenHash.hash(refresh))
                .expiresAt(LocalDateTime.now().plusDays(14))
                .build();
        refreshRepo.save(rt);
        return new AuthResponseDto(access, refresh, "Bearer");
    }

    /**
     * Registers a new user with an encoded password and returns tokens
     */
    public AuthResponseDto register(RegisterRequestDto r) {
        if (users.findByEmail(r.email()).isPresent()) throw new IllegalArgumentException("Email already in use");
        UserAccount u = UserAccount.builder()
                .email(r.email())
                .passwordHash(pe.encode(r.password()))
                .role(r.role().toUpperCase())
                .status("ACTIVE")
                .build();
        users.save(u);
        return issueTokens(u.getEmail());
    }

    /**
     * Authenticates credentials and issues tokens
     */
    public AuthResponseDto login(LoginRequestDto r) {
        Authentication a = am.authenticate(new UsernamePasswordAuthenticationToken(r.email(), r.password()));
        users.findByEmail(r.email()).ifPresent(u -> {
            u.setLastLoginAt(LocalDateTime.now());
            users.save(u);
        });
        return issueTokens(r.email());
    }

    /**
     * Rotates the refresh token if the presented one is valid and still active
     */
    @Transactional
    public AuthResponseDto refresh(String refreshTokenRaw) {
        if (!jwt.isValid(refreshTokenRaw)) throw new IllegalArgumentException("Invalid refresh token");
        String subject = jwt.extractSubject(refreshTokenRaw);
        users.findByEmail(subject).orElseThrow(() -> new IllegalArgumentException("User not found"));

        String th = tokenHash.hash(refreshTokenRaw);
        RefreshToken stored = refreshRepo.findByTokenHash(th).orElseThrow(() -> new IllegalArgumentException("Refresh token not recognized"));
        if (stored.isRevoked() || stored.getExpiresAt().isBefore(LocalDateTime.now()))
            throw new IllegalArgumentException("Refresh token expired or revoked");

        // Rotate: mark old revoked, create new
        stored.setRevoked(true);
        String newAccess = jwt.generateAccessToken(subject);
        String newRefresh = jwt.generateRefreshToken(subject);
        stored.setReplacedBy(tokenHash.hash(newRefresh));
        refreshRepo.save(stored);
        refreshRepo.save(RefreshToken.builder()
                .subject(subject)
                .tokenHash(tokenHash.hash(newRefresh))
                .expiresAt(LocalDateTime.now().plusDays(14))
                .build());
        return new AuthResponseDto(newAccess, newRefresh, "Bearer");
    }
}
