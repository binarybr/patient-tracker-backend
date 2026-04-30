package com.binarysystempatienttracker.patienttracker.security;

import com.binarysystempatienttracker.patienttracker.domain.UserAccount;
import com.binarysystempatienttracker.patienttracker.repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Bridges {@link com.binarysystempatienttracker.patienttracker.domain.UserAccount} to Spring Security's {@link AppUserDetailsService} contract
 * It converts our domain user to a {@link org.springframework.security.core.userdetails.User}
 * with role-based authorities
 */
@Service
public class AppUserDetailsService implements UserDetailsService {
    private final UserRepository users;

    public AppUserDetailsService(UserRepository users) {
        this.users = users;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount u = users.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return User.withUsername(u.getEmail())
                .password(u.getPasswordHash())
                .roles(u.getRole())
                .disabled(!u.isEnabled())
                .accountLocked(!u.isEnabled())
                .build();
    }
}
