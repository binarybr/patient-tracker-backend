package com.binarysystempatienttracker.patienttracker.service;

import com.binarysystempatienttracker.patienttracker.domain.PasswordResetToken;
import com.binarysystempatienttracker.patienttracker.repository.PasswordResetTokenRepository;
import com.binarysystempatienttracker.patienttracker.repository.UserRepository;
import com.binarysystempatienttracker.patienttracker.security.TokenHash;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Password reset flow: issue a one-time token (via email) and consume it to set a new password
 * Raw tokens are never persisted; only their hashes are stored
 */

@AllArgsConstructor
@Service
public class PasswordResetService {
    private final UserRepository users;
    private final PasswordEncoder pe;
    private final JavaMailSender mail;
    private final PasswordResetTokenRepository repo;
    private final TokenHash tokenHash;

    /**
     * Generates a reset token valid for 1 hour and emails it to the user
     *
     * @param email
     */
    public void createResetToken(String email) {
        users.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Unknown email"));
        String raw = UUID.randomUUID().toString();
        repo.save(PasswordResetToken.builder()
                .email(email)
                .tokenHash(tokenHash.hash(raw))
                .expiresAt(LocalDateTime.now().plusHours(1))
                .build());

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(email);
        msg.setSubject("Password Reset");
        msg.setText("Use this token to reset your password: " + raw);
        mail.send(msg);
    }

    /**
     * Validates and consumes a reset token, updating the user's password
     *
     * @param rawToken
     * @param newPassword
     */
    public void resetPassword(String rawToken, String newPassword) {
        var t = repo.findByTokenHash(tokenHash.hash(rawToken))
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));
        if (t.isUsed() || t.getExpiresAt().isBefore(LocalDateTime.now()))
            throw new IllegalArgumentException("Token expired or used");

        var u = users.findByEmail(t.getEmail()).orElseThrow();
        u.setPasswordHash(pe.encode(newPassword));
        users.save(u);

        t.setUsed(true);
        repo.save(t);
    }
}
