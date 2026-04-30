package com.binarysystempatienttracker.patienttracker.web.controller;

import com.binarysystempatienttracker.patienttracker.service.AuthService;
import com.binarysystempatienttracker.patienttracker.service.PasswordResetService;
import com.binarysystempatienttracker.patienttracker.web.dto.AuthResponseDto;
import com.binarysystempatienttracker.patienttracker.web.dto.LoginRequestDto;
import com.binarysystempatienttracker.patienttracker.web.dto.RefreshRequestDto;
import com.binarysystempatienttracker.patienttracker.web.dto.RegisterRequestDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Public authentication endpoints: register, login, refresh token, password reset
 */

@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService auth;
    private final PasswordResetService pwd;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody RegisterRequestDto req){
        return ResponseEntity.ok(auth.register(req));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto req){
        return ResponseEntity.ok(auth.login(req));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDto> refresh(RefreshRequestDto body){
        return ResponseEntity.ok(auth.refresh(body.refreshToken()));
    }

    @PostMapping("forget-password")
    public ResponseEntity<Void> forget(@RequestParam String email){
        pwd.createResetToken(email);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> reset(@RequestParam String token, @RequestParam String newPassword){
        pwd.resetPassword(token, newPassword);
        return ResponseEntity.noContent().build();
    }
}
