package com.poojithairosha.medinotifyapi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

public record UpdateAppointmentNotesRequest(

        @Size(max = 1000, message = "Notes must not exceed 1000 characters")
        @Schema(example = "Patient reported improvement. Follow-up in two weeks.")
        String notes
) {}
