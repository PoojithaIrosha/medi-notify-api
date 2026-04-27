package com.poojithairosha.medinotifyapi.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ConsoleNotificationStrategy implements NotificationStrategy {

    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.CONSOLE;
    }

    @Override
    public void send(NotificationMessage message) {
        log.info("[CONSOLE NOTIFICATION] appointmentId={} | subject='{}' | body='{}' | timestamp={}",
                message.appointmentId(), message.subject(), message.body(), message.timestamp());
    }
}
