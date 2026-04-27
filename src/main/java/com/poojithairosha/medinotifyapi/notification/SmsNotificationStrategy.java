package com.poojithairosha.medinotifyapi.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SmsNotificationStrategy implements NotificationStrategy {

    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.SMS;
    }

    @Override
    public void send(NotificationMessage message) {
        // SMS delivery not yet implemented — placeholder for future integration
        log.info("[SMS NOTIFICATION] appointmentId={} | body='{}' | to patientId={}",
                message.appointmentId(), message.body(), message.patientId());
    }
}
