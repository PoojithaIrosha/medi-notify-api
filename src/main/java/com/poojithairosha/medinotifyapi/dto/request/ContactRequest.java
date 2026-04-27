package com.poojithairosha.medinotifyapi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;

public record ContactRequest(

        @Email(message = "Email must be a valid address")
        @Schema(example = "nimal@example.com")
        String email,

        @Schema(example = "+94771234567")
        String phone
) {}
