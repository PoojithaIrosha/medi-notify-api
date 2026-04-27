package com.poojithairosha.medinotifyapi.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EmailNotificationStrategy implements NotificationStrategy {

    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.EMAIL;
    }

    @Override
    public void send(NotificationMessage message) {
        // Email delivery not yet implemented — placeholder for future integration
        log.info("[EMAIL NOTIFICATION] appointmentId={} | subject='{}' | to patientId={}",
                message.appointmentId(), message.subject(), message.patientId());
    }
}
