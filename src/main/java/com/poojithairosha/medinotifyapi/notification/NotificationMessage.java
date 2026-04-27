package com.poojithairosha.medinotifyapi.notification;

import java.time.Instant;

public record NotificationMessage(
        String appointmentId,
        String patientId,
        String practitionerId,
        String subject,
        String body,
        Instant timestamp
) {
    public static NotificationMessage of(String appointmentId, String patientId,
                                         String practitionerId, String subject, String body) {
        return new NotificationMessage(appointmentId, patientId, practitionerId, subject, body, Instant.now());
    }
}
