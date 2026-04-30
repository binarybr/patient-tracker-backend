package com.binarysystempatienttracker.patienttracker.web.mapper;

import com.binarysystempatienttracker.patienttracker.domain.UserAccount;
import com.binarysystempatienttracker.patienttracker.web.dto.UserResponseDto;
import org.springframework.stereotype.Component;

/**
 * Simple mapping helpers between domain and API DTOs
 */
@Component
public class UserMappers {
    /**
     * Converts {@link UserAccount} to {@link UserResponseDto}
     * @param u
     * @return
     */
    public UserResponseDto toDto(UserAccount u) {
        return new UserResponseDto(
                u.getId(), u.getEmail(), u.getRole(), u.getStatus(),
                u.getDoctor() != null ? u.getDoctor().getId() : null,
                u.getPatient() != null ? u.getPatient().getId() : null
        );
    }
}
