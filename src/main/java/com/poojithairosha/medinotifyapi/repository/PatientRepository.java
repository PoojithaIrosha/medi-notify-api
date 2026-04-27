package com.poojithairosha.medinotifyapi.repository;

import com.poojithairosha.medinotifyapi.model.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends MongoRepository<Patient, String> {

    Optional<Patient> findByNationalId(String nationalId);

    List<Patient> findByPersonDetailsFullNameContainingIgnoreCase(String fullName);

    Page<Patient> findByPersonDetailsFullNameContainingIgnoreCase(String fullName, Pageable pageable);

    boolean existsByNationalId(String nationalId);
}
