package com.poojithairosha.medinotifyapi.controller;

import com.poojithairosha.medinotifyapi.dto.request.CreatePatientRequest;
import com.poojithairosha.medinotifyapi.dto.response.PatientResponse;
import com.poojithairosha.medinotifyapi.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Patients", description = "APIs for creating and searching clinic patients")
@RestController
@RequestMapping(PATIENTS)
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @Operation(summary = "Create a patient", description = "Registers a new patient. National ID must be unique.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Patient created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed on request body"),
            @ApiResponse(responseCode = "409", description = "Patient with the given national ID already exists")
    })
    @PostMapping
    public ResponseEntity<PatientResponse> createPatient(@Valid @RequestBody CreatePatientRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(patientService.createPatient(request));
    }

    @Operation(summary = "Get patient by ID", description = "Returns a single patient record for the given MongoDB document ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Patient found"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    @GetMapping(PATIENTS_BY_ID)
    public ResponseEntity<PatientResponse> getPatientById(@PathVariable String id) {
        return ResponseEntity.ok(patientService.getPatientById(id));
    }

    @Operation(
            summary = "Search patients",
            description = "Search patients by national ID, full name, or both. " +
                    "Returns a paginated list. Supports page, size, and sort query parameters."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search results returned (may be empty)"),
            @ApiResponse(responseCode = "404", description = "No patient found for the given national ID")
    })
    @GetMapping
    public ResponseEntity<Page<PatientResponse>> searchPatients(
            @RequestParam(required = false) String nationalId,
            @RequestParam(required = false) String name,
            @PageableDefault(size = 20, sort = "personDetails.fullName", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(patientService.searchPatients(nationalId, name, pageable));
    }
}
