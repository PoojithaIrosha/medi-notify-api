package com.poojithairosha.medinotifyapi.dto.response;

public record PractitionerResponse(
        String id,
        PersonDetailsResponse personDetails,
        String registrationNo,
        String specialty
) {}
