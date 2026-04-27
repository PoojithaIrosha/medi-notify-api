package com.poojithairosha.medinotifyapi.mapper;

import com.poojithairosha.medinotifyapi.dto.request.CreatePatientRequest;
import com.poojithairosha.medinotifyapi.dto.response.PatientResponse;
import com.poojithairosha.medinotifyapi.model.ContactInfo;
import com.poojithairosha.medinotifyapi.model.Patient;
import com.poojithairosha.medinotifyapi.model.PersonDetails;
import org.springframework.stereotype.Component;

@Component
public class PatientMapper {

    public Patient toDocument(CreatePatientRequest request) {
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

        return Patient.builder()
                .personDetails(personDetails)
                .nationalId(request.nationalId())
                .dateOfBirth(request.dateOfBirth())
                .build();
    }

    public PatientResponse toResponse(Patient patient) {
        return new PatientResponse(
                patient.getId(),
                PersonMapper.toPersonDetailsResponse(patient.getPersonDetails()),
                patient.getNationalId(),
                patient.getDateOfBirth()
        );
    }
}
