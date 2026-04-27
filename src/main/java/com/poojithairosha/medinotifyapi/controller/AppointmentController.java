package com.poojithairosha.medinotifyapi.controller;

import com.poojithairosha.medinotifyapi.dto.request.CreateAppointmentRequest;
import com.poojithairosha.medinotifyapi.dto.request.UpdateAppointmentNotesRequest;
import com.poojithairosha.medinotifyapi.dto.response.AppointmentResponse;
import com.poojithairosha.medinotifyapi.service.AppointmentService;
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

@Tag(name = "Appointments", description = "APIs for booking, viewing, cancelling, and updating appointments")
@RestController
@RequestMapping(APPOINTMENTS)
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @Operation(
            summary = "Create an appointment",
            description = "Books a new appointment for a patient with a practitioner. " +
                    "Validates that the patient and practitioner exist, end time is after start time, " +
                    "and no overlapping BOOKED appointment exists for the same practitioner."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Appointment created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed or end time is not after start time"),
            @ApiResponse(responseCode = "404", description = "Patient or practitioner not found"),
            @ApiResponse(responseCode = "409", description = "Overlapping BOOKED appointment exists for the practitioner")
    })
    @PostMapping
    public ResponseEntity<AppointmentResponse> createAppointment(
            @Valid @RequestBody CreateAppointmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.createAppointment(request));
    }

    @Operation(summary = "Get appointment by ID", description = "Returns a single appointment record for the given MongoDB document ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Appointment found"),
            @ApiResponse(responseCode = "404", description = "Appointment not found")
    })
    @GetMapping(APPOINTMENTS_BY_ID)
    public ResponseEntity<AppointmentResponse> getAppointmentById(@PathVariable String id) {
        return ResponseEntity.ok(appointmentService.getAppointmentById(id));
    }

    @Operation(
            summary = "List appointments",
            description = "Returns a paginated list of appointments. " +
                    "Optionally filter by practitioner ID and/or a specific date (ISO format: YYYY-MM-DD). " +
                    "Supports page, size, and sort query parameters."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Appointment list returned (may be empty)")
    })
    @GetMapping
    public ResponseEntity<Page<AppointmentResponse>> listAppointments(
            @RequestParam(required = false) String practitionerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PageableDefault(size = 20, sort = "startTime", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(appointmentService.listAppointments(practitionerId, date, pageable));
    }

    @Operation(
            summary = "Cancel an appointment",
            description = "Cancels an existing appointment. Only appointments with status BOOKED can be cancelled."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Appointment cancelled successfully"),
            @ApiResponse(responseCode = "400", description = "Appointment is not in BOOKED status"),
            @ApiResponse(responseCode = "404", description = "Appointment not found")
    })
    @PatchMapping(APPOINTMENTS_CANCEL_BY_ID)
    public ResponseEntity<AppointmentResponse> cancelAppointment(@PathVariable String id) {
        return ResponseEntity.ok(appointmentService.cancelAppointment(id));
    }

    @Operation(summary = "Update appointment notes", description = "Replaces the notes on an existing appointment. Notes are optional and can be cleared by passing null.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notes updated successfully"),
            @ApiResponse(responseCode = "400", description = "Notes exceed maximum allowed length"),
            @ApiResponse(responseCode = "404", description = "Appointment not found")
    })
    @PatchMapping(APPOINTMENTS_BY_ID)
    public ResponseEntity<AppointmentResponse> updateAppointmentNotes(
            @PathVariable String id,
            @Valid @RequestBody UpdateAppointmentNotesRequest request) {
        return ResponseEntity.ok(appointmentService.updateAppointmentNotes(id, request));
    }
}
