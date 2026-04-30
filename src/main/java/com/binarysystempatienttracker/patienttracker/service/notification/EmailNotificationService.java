package com.binarysystempatienttracker.patienttracker.service.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Slf4j
@Primary
@Service
public class EmailNotificationService implements NotificationService {

    @Override
    public void notifyUser(String to, String message) {
        log.info("[EMAIL] To={} {}", to, message);
    }
}

