package com.poojithairosha.medinotifyapi.controller;

import com.poojithairosha.medinotifyapi.dto.request.CreateAppointmentRequest;
import com.poojithairosha.medinotifyapi.dto.request.UpdateAppointmentNotesRequest;
import com.poojithairosha.medinotifyapi.dto.response.AppointmentResponse;
import com.poojithairosha.medinotifyapi.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

import static com.poojithairosha.medinotifyapi.controller.ApiUrls.AppointmentsUrl.*;

@RestController
@RequestMapping(APPOINTMENTS)
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<AppointmentResponse> createAppointment(
            @Valid @RequestBody CreateAppointmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.createAppointment(request));
    }

    @GetMapping(APPOINTMENTS_BY_ID)
    public ResponseEntity<AppointmentResponse> getAppointmentById(@PathVariable String id) {
        return ResponseEntity.ok(appointmentService.getAppointmentById(id));
    }

    @GetMapping
    public ResponseEntity<Page<AppointmentResponse>> listAppointments(
            @RequestParam(required = false) String practitionerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PageableDefault(size = 20, sort = "startTime", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(appointmentService.listAppointments(practitionerId, date, pageable));
    }

    @PatchMapping(APPOINTMENTS_CANCEL_BY_ID)
    public ResponseEntity<AppointmentResponse> cancelAppointment(@PathVariable String id) {
        return ResponseEntity.ok(appointmentService.cancelAppointment(id));
    }

    @PatchMapping(APPOINTMENTS_BY_ID)
    public ResponseEntity<AppointmentResponse> updateAppointmentNotes(
            @PathVariable String id,
            @Valid @RequestBody UpdateAppointmentNotesRequest request) {
        return ResponseEntity.ok(appointmentService.updateAppointmentNotes(id, request));
    }
}
