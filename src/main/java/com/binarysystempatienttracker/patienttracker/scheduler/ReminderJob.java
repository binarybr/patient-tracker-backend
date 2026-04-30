//package com.binarysystempatienttracker.patienttracker.scheduler;
//
//import com.binarysystempatienttracker.patienttracker.repository.AppointmentRepository;
//import lombok.AllArgsConstructor;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDateTime;
//
///**
// * Periodic job that emails reminders for appointments occurring in the next hour
// * The actual recipient is hard-coded for demo. Replace with real patient email lookup
// * when integration with a notification service
// */
//@AllArgsConstructor
//@Component
//public class ReminderJob {
//    private final AppointmentRepository appts;
//
//    private final JavaMailSender mail;
//
//    /**
//     * Runs at the top os every hour (cron: second minute hour day month day-of-week)
//     */
//    @Scheduled(cron = "00")
//    public void sendUpcomingApptReminders() {
//        var now = LocalDateTime.now();
//        var next = now.plusHours(1);
//        var list = appts.findByApptTimeBetween(now, next);
//
//        for (var a : list) {
//            SimpleMailMessage msg = new SimpleMailMessage();
////            inject actual patient email when available
//            msg.setTo("patient@example.com");
//            msg.setSubject("Appointment Reminder");
//            msg.setText("Reminder: appointment at " + a.getApptTime());
//            mail.send(msg);
//        }
//    }
//
//}
