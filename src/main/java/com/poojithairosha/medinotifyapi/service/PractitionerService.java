package com.poojithairosha.medinotifyapi.service;

import com.poojithairosha.medinotifyapi.dto.request.CreatePractitionerRequest;
import com.poojithairosha.medinotifyapi.dto.response.PractitionerResponse;
import com.poojithairosha.medinotifyapi.exception.ConflictException;
import com.poojithairosha.medinotifyapi.exception.ResourceNotFoundException;
import com.poojithairosha.medinotifyapi.mapper.PractitionerMapper;
import com.poojithairosha.medinotifyapi.model.Practitioner;
import com.poojithairosha.medinotifyapi.repository.PractitionerRepository;
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
public class PractitionerService {

    private final PractitionerRepository practitionerRepository;
    private final PractitionerMapper practitionerMapper;

    public PractitionerResponse createPractitioner(CreatePractitionerRequest request) {
        log.info("Creating practitioner with registrationNo: {}", request.registrationNo());

        if (practitionerRepository.existsByRegistrationNo(request.registrationNo())) {
            throw new ConflictException("Practitioner with registrationNo '" + request.registrationNo() + "' already exists.");
        }

        Practitioner saved = practitionerRepository.save(practitionerMapper.toDocument(request));
        log.info("Practitioner created successfully with id: {}", saved.getId());

        return practitionerMapper.toResponse(saved);
    }

    public PractitionerResponse getPractitionerById(String id) {
        log.debug("Fetching practitioner by id: {}", id);
        return practitionerMapper.toResponse(getPractitionerEntityById(id));
    }

    public Page<PractitionerResponse> listPractitioners(String registrationNo, String name, Pageable pageable) {
        if (registrationNo != null && name != null) {
            log.debug("Searching practitioner by registrationNo and name containing: {}", name);
            Practitioner practitioner = practitionerRepository.findByRegistrationNo(registrationNo)
                    .orElseThrow(() -> new ResourceNotFoundException("Practitioner", "registrationNo", registrationNo));

            String fullName = practitioner.getPersonDetails().getFullName();
            if (!fullName.toLowerCase().contains(name.toLowerCase())) {
                return Page.empty(pageable);
            }
            return toSingleElementPage(practitioner, pageable);
        }

                if (registrationNo != null) {
            log.debug("Searching practitioner by registrationNo: {}", registrationNo);
            Practitioner practitioner = practitionerRepository.findByRegistrationNo(registrationNo)
                    .orElseThrow(() -> new ResourceNotFoundException("Practitioner", "registrationNo", registrationNo));
            return toSingleElementPage(practitioner, pageable);
        }

        if (name != null) {
            log.debug("Searching practitioners by name containing: {}", name);
            return practitionerRepository
                    .findByPersonDetailsFullNameContainingIgnoreCase(name, pageable)
                    .map(practitionerMapper::toResponse);
        }

        log.debug("Listing all practitioners - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        return practitionerRepository.findAll(pageable).map(practitionerMapper::toResponse);
    }

    // Reusable by AppointmentService to validate practitioner existence
    public void validatePractitionerExists(String practitionerId) {
        if (!practitionerRepository.existsById(practitionerId)) {
            throw new ResourceNotFoundException("Practitioner", "id", practitionerId);
        }
    }

    // Reusable by AppointmentService when the full entity is needed
    public Practitioner getPractitionerEntityById(String practitionerId) {
        return practitionerRepository.findById(practitionerId)
                .orElseThrow(() -> new ResourceNotFoundException("Practitioner", "id", practitionerId));
    }

    private Page<PractitionerResponse> toSingleElementPage(Practitioner practitioner, Pageable pageable) {
        return new PageImpl<>(List.of(practitionerMapper.toResponse(practitioner)), pageable, 1);
    }
}
