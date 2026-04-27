package com.poojithairosha.medinotifyapi.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePractitionerRequest(

        @NotBlank(message = "Full name is required")
        @Size(min = 3, message = "Full name must be at least 3 characters")
        String fullName,

        @Valid
        ContactRequest contact,

        @NotBlank(message = "Registration number is required")
        String registrationNo,

        @NotBlank(message = "Specialty is required")
        String specialty
) {}
