package com.poojithairosha.medinotifyapi.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "practitioners")
public class Practitioner {

    @Id
    private String id;

    @Valid
    @NotNull(message = "Person details are required")
    private PersonDetails personDetails;

    @NotBlank(message = "Registration number is required")
    @Indexed(unique = true)
    private String registrationNo;

    @NotBlank(message = "Specialty is required")
    private String specialty;
}
