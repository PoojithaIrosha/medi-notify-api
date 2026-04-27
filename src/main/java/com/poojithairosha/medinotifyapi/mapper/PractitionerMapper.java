package com.poojithairosha.medinotifyapi.mapper;

import com.poojithairosha.medinotifyapi.dto.request.CreatePractitionerRequest;
import com.poojithairosha.medinotifyapi.dto.response.PractitionerResponse;
import com.poojithairosha.medinotifyapi.model.ContactInfo;
import com.poojithairosha.medinotifyapi.model.PersonDetails;
import com.poojithairosha.medinotifyapi.model.Practitioner;
import org.springframework.stereotype.Component;

@Component
public class PractitionerMapper {

    public Practitioner toDocument(CreatePractitionerRequest request) {
        ContactInfo contact = request.contact() != null
                ? ContactInfo.builder()
                        .email(request.contact().email())
                        .phone(request.contact().phone())
                        .build()
                : null;

        PersonDetails personDetails = PersonDetails.builder()
                .fullName(request.fullName())
                .contact(contact)
                .build();

        return Practitioner.builder()
                .personDetails(personDetails)
                .registrationNo(request.registrationNo())
                .specialty(request.specialty())
                .build();
    }

    public PractitionerResponse toResponse(Practitioner practitioner) {
        return new PractitionerResponse(
                practitioner.getId(),
                PersonMapper.toPersonDetailsResponse(practitioner.getPersonDetails()),
                practitioner.getRegistrationNo(),
                practitioner.getSpecialty()
        );
    }
}
