package com.poojithairosha.medinotifyapi.controller;

import com.poojithairosha.medinotifyapi.dto.request.CreatePatientRequest;
import com.poojithairosha.medinotifyapi.dto.response.PatientResponse;
import com.poojithairosha.medinotifyapi.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.poojithairosha.medinotifyapi.controller.ApiUrls.PatientsUrls.PATIENTS;
import static com.poojithairosha.medinotifyapi.controller.ApiUrls.PatientsUrls.PATIENTS_BY_ID;

@RestController
@RequestMapping(PATIENTS)
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    public ResponseEntity<PatientResponse> createPatient(@Valid @RequestBody CreatePatientRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(patientService.createPatient(request));
    }

    @GetMapping(PATIENTS_BY_ID)
    public ResponseEntity<PatientResponse> getPatientById(@PathVariable String id) {
        return ResponseEntity.ok(patientService.getPatientById(id));
    }

    @GetMapping
    public ResponseEntity<Page<PatientResponse>> searchPatients(
            @RequestParam(required = false) String nationalId,
            @RequestParam(required = false) String name,
            @PageableDefault(size = 20, sort = "personDetails.fullName", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(patientService.searchPatients(nationalId, name, pageable));
    }
}
