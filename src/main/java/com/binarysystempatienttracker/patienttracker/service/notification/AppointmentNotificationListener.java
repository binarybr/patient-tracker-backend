package com.binarysystempatienttracker.patienttracker.service.notification;

import com.binarysystempatienttracker.patienttracker.domain.events.AppointmentCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppointmentNotificationListener {

    private final NotificationService notificationService;

    @EventListener
    public void handle(AppointmentCreatedEvent event) {
        notificationService.notifyUser(
                "doctor",
                "New appointment scheduled at " + event.apptTime()
        );

        notificationService.notifyUser(
                "patient",
                "Your appointment is confirmed"
        );
    }
}
