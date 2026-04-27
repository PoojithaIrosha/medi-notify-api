package com.poojithairosha.medinotifyapi.controller;

import com.poojithairosha.medinotifyapi.dto.request.CreatePractitionerRequest;
import com.poojithairosha.medinotifyapi.dto.response.PractitionerResponse;
import com.poojithairosha.medinotifyapi.service.PractitionerService;
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

import static com.poojithairosha.medinotifyapi.controller.ApiUrls.PractitionersUrls.PRACTITIONERS;
import static com.poojithairosha.medinotifyapi.controller.ApiUrls.PractitionersUrls.PRACTITIONERS_BY_ID;

@Tag(name = "Practitioners", description = "APIs for creating and managing clinic practitioners")
@RestController
@RequestMapping(PRACTITIONERS)
@RequiredArgsConstructor
public class PractitionerController {

    private final PractitionerService practitionerService;

    @Operation(summary = "Create a practitioner", description = "Registers a new practitioner. Registration number must be unique.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Practitioner created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed on request body"),
            @ApiResponse(responseCode = "409", description = "Practitioner with the given registration number already exists")
    })
    @PostMapping
    public ResponseEntity<PractitionerResponse> createPractitioner(@Valid @RequestBody CreatePractitionerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(practitionerService.createPractitioner(request));
    }

    @Operation(summary = "Get practitioner by ID", description = "Returns a single practitioner record for the given MongoDB document ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Practitioner found"),
            @ApiResponse(responseCode = "404", description = "Practitioner not found")
    })
    @GetMapping(PRACTITIONERS_BY_ID)
    public ResponseEntity<PractitionerResponse> getPractitionerById(@PathVariable String id) {
        return ResponseEntity.ok(practitionerService.getPractitionerById(id));
    }

    @Operation(
            summary = "List practitioners",
            description = "Returns a paginated list of practitioners. " +
                    "Optionally filter by registration number or full name. " +
                    "Supports page, size, and sort query parameters."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Practitioner list returned (may be empty)"),
            @ApiResponse(responseCode = "404", description = "No practitioner found for the given registration number")
    })
    @GetMapping
    public ResponseEntity<Page<PractitionerResponse>> listPractitioners(
            @RequestParam(required = false) String registrationNo,
            @RequestParam(required = false) String name,
            @PageableDefault(size = 20, sort = "personDetails.fullName", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(practitionerService.listPractitioners(registrationNo, name, pageable));
    }
}
