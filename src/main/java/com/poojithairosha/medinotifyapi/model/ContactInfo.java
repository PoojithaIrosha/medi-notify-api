package com.poojithairosha.medinotifyapi.model;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactInfo {

    @Email(message = "Email must be a valid address")
    private String email;

    private String phone;
}
