package com.poojithairosha.medinotifyapi.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreatePatientRequest(

        @NotBlank(message = "Full name is required")
        @Size(min = 3, message = "Full name must be at least 3 characters")
        String fullName,

        @Valid
        ContactRequest contact,

        @NotBlank(message = "National ID is required")
        String nationalId,

        @NotNull(message = "Date of birth is required")
        LocalDate dateOfBirth
) {}
