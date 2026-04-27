package com.poojithairosha.medinotifyapi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePractitionerRequest(

        @NotBlank(message = "Full name is required")
        @Size(min = 3, message = "Full name must be at least 3 characters")
        @Schema(example = "Dr. Rohan Perera")
        String fullName,

        @Valid
        ContactRequest contact,

        @NotBlank(message = "Registration number is required")
        @Schema(example = "SLMC-1001")
        String registrationNo,

        @NotBlank(message = "Specialty is required")
        @Schema(example = "Cardiology")
        String specialty
) {}
