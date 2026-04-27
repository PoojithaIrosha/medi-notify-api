package com.poojithairosha.medinotifyapi.service;

import com.poojithairosha.medinotifyapi.dto.request.CreatePatientRequest;
import com.poojithairosha.medinotifyapi.dto.response.PatientResponse;
import com.poojithairosha.medinotifyapi.exception.ConflictException;
import com.poojithairosha.medinotifyapi.exception.ResourceNotFoundException;
import com.poojithairosha.medinotifyapi.mapper.PatientMapper;
import com.poojithairosha.medinotifyapi.model.Patient;
import com.poojithairosha.medinotifyapi.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    public PatientResponse createPatient(CreatePatientRequest request) {
        log.info("Creating patient with nationalId: {}", request.nationalId());

        if (patientRepository.existsByNationalId(request.nationalId())) {
            throw new ConflictException("Patient with nationalId '" + request.nationalId() + "' already exists.");
        }

        Patient saved = patientRepository.save(patientMapper.toDocument(request));
        log.info("Patient created successfully with id: {}", saved.getId());

        return patientMapper.toResponse(saved);
    }

    public PatientResponse getPatientById(String id) {
        log.debug("Fetching patient by id: {}", id);
        return patientMapper.toResponse(getPatientEntityById(id));
    }

    public Page<PatientResponse> searchPatients(String nationalId, String name, Pageable pageable) {
        if (nationalId != null && name != null) {
            log.debug("Searching patient by nationalId and name containing: {}", name);
            Patient patient = patientRepository.findByNationalId(nationalId)
                    .orElseThrow(() -> new ResourceNotFoundException("Patient", "nationalId", nationalId));

            String fullName = patient.getPersonDetails().getFullName();
            if (!fullName.toLowerCase().contains(name.toLowerCase())) {
                return Page.empty(pageable);
            }
            return toSingleElementPage(patient, pageable);
        }

        if (nationalId != null) {
            log.debug("Searching patient by nationalId: {}", nationalId);
            Patient patient = patientRepository.findByNationalId(nationalId)
                    .orElseThrow(() -> new ResourceNotFoundException("Patient", "nationalId", nationalId));
            return toSingleElementPage(patient, pageable);
        }

        if (name != null) {
            log.debug("Searching patients by name containing: {}", name);
            return patientRepository
                    .findByPersonDetailsFullNameContainingIgnoreCase(name, pageable)
                    .map(patientMapper::toResponse);
        }

        log.debug("Listing all patients - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        return patientRepository.findAll(pageable).map(patientMapper::toResponse);
    }

    private Page<PatientResponse> toSingleElementPage(Patient patient, Pageable pageable) {
        return new PageImpl<>(List.of(patientMapper.toResponse(patient)), pageable, 1);
    }

    // Reusable by other services (e.g. AppointmentService) to validate patient existence
    public void validatePatientExists(String patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient", "id", patientId);
        }
    }

    // Reusable by other services that need the full entity (e.g. for enriched responses)
    public Patient getPatientEntityById(String patientId) {
        return patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", patientId));
    }
}
