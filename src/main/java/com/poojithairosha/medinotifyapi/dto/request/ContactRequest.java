package com.poojithairosha.medinotifyapi.dto.request;

import jakarta.validation.constraints.Email;

public record ContactRequest(

        @Email(message = "Email must be a valid address")
        String email,

        String phone
) {}
