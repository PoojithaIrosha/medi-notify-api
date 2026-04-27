package com.poojithairosha.medinotifyapi.controller;

import com.poojithairosha.medinotifyapi.dto.request.CreatePractitionerRequest;
import com.poojithairosha.medinotifyapi.dto.response.PractitionerResponse;
import com.poojithairosha.medinotifyapi.service.PractitionerService;
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

@RestController
@RequestMapping("/api/practitioners")
@RequiredArgsConstructor
public class PractitionerController {

    private final PractitionerService practitionerService;

    @PostMapping
    public ResponseEntity<PractitionerResponse> createPractitioner(@Valid @RequestBody CreatePractitionerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(practitionerService.createPractitioner(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PractitionerResponse> getPractitionerById(@PathVariable String id) {
        return ResponseEntity.ok(practitionerService.getPractitionerById(id));
    }

    @GetMapping
    public ResponseEntity<Page<PractitionerResponse>> listPractitioners(
            @RequestParam(required = false) String registrationNo,
            @RequestParam(required = false) String name,
            @PageableDefault(size = 20, sort = "personDetails.fullName", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(practitionerService.listPractitioners(registrationNo, name, pageable));
    }
}
