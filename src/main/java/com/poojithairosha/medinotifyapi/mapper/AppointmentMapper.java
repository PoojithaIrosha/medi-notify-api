package com.poojithairosha.medinotifyapi.mapper;

import com.poojithairosha.medinotifyapi.dto.request.CreateAppointmentRequest;
import com.poojithairosha.medinotifyapi.dto.response.AppointmentResponse;
import com.poojithairosha.medinotifyapi.model.Appointment;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {

    public Appointment toDocument(CreateAppointmentRequest request) {
        return Appointment.builder()
                .patientId(request.patientId())
                .practitionerId(request.practitionerId())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .notes(request.notes())
                .build();
    }

    public AppointmentResponse toResponse(Appointment appointment) {
        return new AppointmentResponse(
                appointment.getId(),
                appointment.getPatientId(),
                appointment.getPractitionerId(),
                appointment.getStartTime(),
                appointment.getEndTime(),
                appointment.getStatus(),
                appointment.getNotes(),
                appointment.getCreatedAt(),
                appointment.getUpdatedAt()
        );
    }
}
