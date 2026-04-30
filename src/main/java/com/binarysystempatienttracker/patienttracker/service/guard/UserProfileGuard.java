package com.binarysystempatienttracker.patienttracker.service.guard;

import com.binarysystempatienttracker.patienttracker.domain.Doctor;
import com.binarysystempatienttracker.patienttracker.domain.Patient;
import com.binarysystempatienttracker.patienttracker.domain.UserAccount;
import com.binarysystempatienttracker.patienttracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequiredArgsConstructor
public class UserProfileGuard {

    private final UserRepository userRepository;

    public UserAccount loadUser(UserDetails ud) {
        return userRepository.findByEmail(ud.getUsername())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "User not found"
                ));
    }

    public Patient requirePatient(UserAccount user) {
        if (user.getPatient() == null)
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "User is not linked to a patient profile"
            );
        return user.getPatient();
    }

    public Doctor requireDoctor(UserAccount user) {
        if (user.getDoctor() == null)
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "User is not linked to a doctor profile"
            );
        return user.getDoctor();
    }
}
