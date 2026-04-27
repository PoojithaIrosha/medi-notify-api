package com.poojithairosha.medinotifyapi.notification;

public interface NotificationStrategy {

    NotificationChannel getChannel();

    void send(NotificationMessage message);
}
