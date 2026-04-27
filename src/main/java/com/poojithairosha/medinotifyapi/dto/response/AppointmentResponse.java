package com.poojithairosha.medinotifyapi.dto.response;

import com.poojithairosha.medinotifyapi.model.AppointmentStatus;

import java.time.Instant;

public record AppointmentResponse(
        String id,
        String patientId,
        String practitionerId,
        Instant startTime,
        Instant endTime,
        AppointmentStatus status,
        String notes,
        Instant createdAt,
        Instant updatedAt
) {}
