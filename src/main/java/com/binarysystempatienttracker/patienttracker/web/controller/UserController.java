package com.binarysystempatienttracker.patienttracker.web.controller;

import com.binarysystempatienttracker.patienttracker.domain.UserAccount;
import com.binarysystempatienttracker.patienttracker.repository.UserRepository;
import com.binarysystempatienttracker.patienttracker.service.UserService;
import com.binarysystempatienttracker.patienttracker.web.dto.ChangePasswordRequestDto;
import com.binarysystempatienttracker.patienttracker.web.dto.LinkProfileRequestDto;
import com.binarysystempatienttracker.patienttracker.web.dto.UpdateUserRequestDto;
import com.binarysystempatienttracker.patienttracker.web.dto.UserResponseDto;
import com.binarysystempatienttracker.patienttracker.web.mapper.UserMappers;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin endpoints to manage users and self-service endpoints for current user
 */
@AllArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final UserRepository users;
    private final UserMappers mapper;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping()
    public List<UserResponseDto> list() {
        return userService.list().stream().map(mapper::toDto).toList();
    }

    @PreAuthorize(("hasRole('ADMIN')"))
    @GetMapping("/{id}")
    public UserResponseDto get(@PathVariable Long id) {
        return mapper.toDto(userService.get(id));
    }

    @PreAuthorize(("hasRole('ADMIN')"))
    @PostMapping
    public ResponseEntity<UserResponseDto> create(@Valid @RequestBody UpdateUserRequestDto req) {
        return ResponseEntity.ok(mapper.toDto(userService.create(req)));
    }

    @PreAuthorize(("hasRole('ADMIN')"))
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> update(Long id, UpdateUserRequestDto req) {
        return ResponseEntity.ok(mapper.toDto(userService.update(id, req)));
    }

    @PreAuthorize(("hasRole('ADMIN')"))
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Returns basic details of  the authenticated account
     * @param ud
     * @return
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> me(@AuthenticationPrincipal UserDetails ud) {
        UserAccount u = users.findByEmail(ud.getUsername()).orElseThrow();
        return ResponseEntity.ok(new UserResponseDto(
                u.getId(), u.getEmail(), u.getRole(), u.getStatus(),
                u.getDoctor() != null ? u.getDoctor().getId() : null,
                u.getPatient() != null ? u.getPatient().getId() : null
        ));
    }

    /**
     * Change current user's password
     * @param ud
     * @param req
     * @return
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me/password")
    public ResponseEntity<Void> changePassword(UserDetails ud, ChangePasswordRequestDto req) {
        UserAccount u = users.findByEmail(ud.getUsername()).orElseThrow();
        userService.changePassword(u, req.oldPassword(), req.newPassword());
        return ResponseEntity.noContent().build();
    }

    /**
     * Link current user to doctor/patient profiles
     * @param ud
     * @param req
     * @return
     */
    @PreAuthorize(("isAuthenticated()"))
    @PatchMapping("/me/link")
    public ResponseEntity<UserResponseDto> link(@AuthenticationPrincipal UserDetails ud, @Valid @RequestBody LinkProfileRequestDto req) {
        UserAccount u = users.findByEmail(ud.getUsername()).orElseThrow();
        u = userService.linkProfile(u, req.doctorId(), req.patientId());
        return ResponseEntity.ok(new UserResponseDto(
                u.getId(), u.getEmail(), u.getRole(), u.getStatus(),
                u.getDoctor() != null ? u.getDoctor().getId() : null,
                u.getPatient() != null ? u.getPatient().getId() : null
        ));
    }
}
