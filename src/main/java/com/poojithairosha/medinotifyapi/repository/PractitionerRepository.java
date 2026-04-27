package com.poojithairosha.medinotifyapi.repository;

import com.poojithairosha.medinotifyapi.model.Practitioner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface PractitionerRepository extends MongoRepository<Practitioner, String> {

    Optional<Practitioner> findByRegistrationNo(String registrationNo);

    List<Practitioner> findByPersonDetailsFullNameContainingIgnoreCase(String fullName);

    Page<Practitioner> findByPersonDetailsFullNameContainingIgnoreCase(String fullName, Pageable pageable);

    boolean existsByRegistrationNo(String registrationNo);
}
