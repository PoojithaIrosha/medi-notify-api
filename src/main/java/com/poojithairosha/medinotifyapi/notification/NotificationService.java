package com.poojithairosha.medinotifyapi.notification;

import com.poojithairosha.medinotifyapi.model.Appointment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final NotificationChannel DEFAULT_CHANNEL = NotificationChannel.CONSOLE;

    private final NotificationStrategyFactory strategyFactory;

    public void notifyAppointmentBooked(Appointment appointment) {
        log.debug("Sending booking notification for appointmentId: {}", appointment.getId());
        NotificationMessage message = NotificationMessage.of(
                appointment.getId(),
                appointment.getPatientId(),
                appointment.getPractitionerId(),
                "Appointment Confirmed",
                String.format("Your appointment has been booked. Appointment ID: %s, Start: %s, End: %s",
                        appointment.getId(), appointment.getStartTime(), appointment.getEndTime())
        );
        send(message);
    }

    public void notifyAppointmentCancelled(Appointment appointment) {
        log.debug("Sending cancellation notification for appointmentId: {}", appointment.getId());
        NotificationMessage message = NotificationMessage.of(
                appointment.getId(),
                appointment.getPatientId(),
                appointment.getPractitionerId(),
                "Appointment Cancelled",
                String.format("Your appointment has been cancelled. Appointment ID: %s, Start: %s",
                        appointment.getId(), appointment.getStartTime())
        );
        send(message);
    }

    public void notifyAppointmentUpdated(Appointment appointment) {
        log.debug("Sending update notification for appointmentId: {}", appointment.getId());
        NotificationMessage message = NotificationMessage.of(
                appointment.getId(),
                appointment.getPatientId(),
                appointment.getPractitionerId(),
                "Appointment Updated",
                String.format("Your appointment notes have been updated. Appointment ID: %s",
                        appointment.getId())
        );
        send(message);
    }

    private void send(NotificationMessage message) {
        strategyFactory.getStrategy(DEFAULT_CHANNEL).send(message);
    }
}
