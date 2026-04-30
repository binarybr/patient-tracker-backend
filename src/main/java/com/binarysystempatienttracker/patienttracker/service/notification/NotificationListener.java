package com.binarysystempatienttracker.patienttracker.service.notification;

import com.binarysystempatienttracker.patienttracker.domain.events.AppointmentCreatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class NotificationListener {

    @EventListener
    public void onAppointmentCreated(AppointmentCreatedEvent event) {
        // Phase 2 = log / mock notification
        log.info("Notify doctor {} and patient {} for appointment {} at {}",
                event.doctorId(),
                event.patientId(),
                event.appointmentId(),
                event.apptTime());
    }
}
