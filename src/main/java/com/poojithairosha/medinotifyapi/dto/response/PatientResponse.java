package com.poojithairosha.medinotifyapi.dto.response;

import java.time.LocalDate;

public record PatientResponse(
        String id,
        PersonDetailsResponse personDetails,
        String nationalId,
        LocalDate dateOfBirth
) {}
