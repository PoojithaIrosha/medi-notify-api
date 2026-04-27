package com.poojithairosha.medinotifyapi.mapper;

import com.poojithairosha.medinotifyapi.dto.response.ContactResponse;
import com.poojithairosha.medinotifyapi.dto.response.PersonDetailsResponse;
import com.poojithairosha.medinotifyapi.model.ContactInfo;
import com.poojithairosha.medinotifyapi.model.PersonDetails;

final class PersonMapper {

    private PersonMapper() {}

    static PersonDetailsResponse toPersonDetailsResponse(PersonDetails personDetails) {
        if (personDetails == null) {
            return null;
        }
        return new PersonDetailsResponse(
                personDetails.getFullName(),
                toContactResponse(personDetails.getContact())
        );
    }

    static ContactResponse toContactResponse(ContactInfo contact) {
        if (contact == null) {
            return null;
        }
        return new ContactResponse(contact.getEmail(), contact.getPhone());
    }
}
