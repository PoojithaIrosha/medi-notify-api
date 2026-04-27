package com.poojithairosha.medinotifyapi.dto.request;

import jakarta.validation.constraints.Size;

public record UpdateAppointmentNotesRequest(

        @Size(max = 1000, message = "Notes must not exceed 1000 characters")
        String notes
) {}
