package com.binarysystempatienttracker.patienttracker.service.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SmsNotificationService implements NotificationService {

    @Override
    public void notifyUser(String to, String message) {
        log.info("[SMS] To={} {}", to, message);
    }
}