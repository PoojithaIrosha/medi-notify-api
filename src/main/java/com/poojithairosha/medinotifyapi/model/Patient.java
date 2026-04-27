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

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "patients")
public class Patient {

    @Id
    private String id;

    @Valid
    @NotNull(message = "Person details are required")
    private PersonDetails personDetails;

    @NotBlank(message = "National ID is required")
    @Indexed(unique = true)
    private String nationalId;

    @NotNull(message = "Date of birth is required")
    private LocalDate dateOfBirth;
}
