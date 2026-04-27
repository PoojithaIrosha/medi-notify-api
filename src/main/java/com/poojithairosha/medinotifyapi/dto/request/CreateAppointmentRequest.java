package com.poojithairosha.medinotifyapi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record CreateAppointmentRequest(

        @NotBlank(message = "Patient ID is required")
        @Schema(example = "662f1a1b2c3d4e5f67890123")
        String patientId,

        @NotBlank(message = "Practitioner ID is required")
        @Schema(example = "662f1a1b2c3d4e5f67890124")
        String practitionerId,

        @NotNull(message = "Start time is required")
        @Schema(example = "2026-05-01T09:00:00Z")
        Instant startTime,

        @NotNull(message = "End time is required")
        @Schema(example = "2026-05-01T10:00:00Z")
        Instant endTime,

        @Schema(example = "Initial consultation")
        String notes
) {}
