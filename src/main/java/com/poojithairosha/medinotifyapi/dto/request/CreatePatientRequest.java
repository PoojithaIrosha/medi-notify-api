package com.poojithairosha.medinotifyapi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreatePatientRequest(

        @NotBlank(message = "Full name is required")
        @Size(min = 3, message = "Full name must be at least 3 characters")
        @Schema(example = "Nimal Perera")
        String fullName,

        @Valid
        ContactRequest contact,

        @NotBlank(message = "National ID is required")
        @Schema(example = "901234567V")
        String nationalId,

        @NotNull(message = "Date of birth is required")
        @Schema(example = "1990-05-12")
        LocalDate dateOfBirth
) {}
