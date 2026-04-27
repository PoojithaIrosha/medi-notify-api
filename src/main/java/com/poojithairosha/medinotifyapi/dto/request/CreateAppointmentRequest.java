package com.poojithairosha.medinotifyapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record CreateAppointmentRequest(

        @NotBlank(message = "Patient ID is required")
        String patientId,

        @NotBlank(message = "Practitioner ID is required")
        String practitionerId,

        @NotNull(message = "Start time is required")
        Instant startTime,

        @NotNull(message = "End time is required")
        Instant endTime,

        String notes
) {}
