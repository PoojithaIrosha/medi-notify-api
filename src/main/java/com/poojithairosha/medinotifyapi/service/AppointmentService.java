package com.poojithairosha.medinotifyapi.service;

import com.poojithairosha.medinotifyapi.dto.request.CreateAppointmentRequest;
import com.poojithairosha.medinotifyapi.dto.request.UpdateAppointmentNotesRequest;
import com.poojithairosha.medinotifyapi.dto.response.AppointmentResponse;
import com.poojithairosha.medinotifyapi.exception.BadRequestException;
import com.poojithairosha.medinotifyapi.exception.ConflictException;
import com.poojithairosha.medinotifyapi.exception.ResourceNotFoundException;
import com.poojithairosha.medinotifyapi.mapper.AppointmentMapper;
import com.poojithairosha.medinotifyapi.model.Appointment;
import com.poojithairosha.medinotifyapi.model.AppointmentStatus;
import com.poojithairosha.medinotifyapi.notification.NotificationService;
import com.poojithairosha.medinotifyapi.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;
    private final PatientService patientService;
    private final PractitionerService practitionerService;
    private final NotificationService notificationService;

    public AppointmentResponse createAppointment(CreateAppointmentRequest request) {
        log.info("Creating appointment for patientId: {} with practitionerId: {}",
                request.patientId(), request.practitionerId());

        patientService.validatePatientExists(request.patientId());
        practitionerService.validatePractitionerExists(request.practitionerId());

        if (!request.endTime().isAfter(request.startTime())) {
            throw new BadRequestException("End time must be after start time.");
        }

        if (appointmentRepository.existsOverlappingAppointment(
                request.practitionerId(), request.startTime(), request.endTime())) {
            throw new ConflictException(
                    "Practitioner already has a BOOKED appointment overlapping the requested time slot.");
        }

        Appointment appointment = appointmentMapper.toDocument(request);
        appointment.setStatus(AppointmentStatus.BOOKED);

        Appointment saved = appointmentRepository.save(appointment);
        log.info("Appointment created with id: {}", saved.getId());

        notificationService.notifyAppointmentBooked(saved);
        return appointmentMapper.toResponse(saved);
    }

    public AppointmentResponse getAppointmentById(String id) {
        log.debug("Fetching appointment by id: {}", id);
        return appointmentMapper.toResponse(getAppointmentEntityById(id));
    }

    public Page<AppointmentResponse> listAppointments(String practitionerId, LocalDate date, Pageable pageable) {
        if (practitionerId != null && date != null) {
            Instant from = date.atStartOfDay(ZoneOffset.UTC).toInstant();
            Instant to = date.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
            log.debug("Listing appointments for practitionerId: {} on date: {}", practitionerId, date);
            return appointmentRepository
                    .findByPractitionerIdAndStartTimeBetween(practitionerId, from, to, pageable)
                    .map(appointmentMapper::toResponse);
        }

        if (practitionerId != null) {
            log.debug("Listing appointments for practitionerId: {}", practitionerId);
            return appointmentRepository
                    .findByPractitionerId(practitionerId, pageable)
                    .map(appointmentMapper::toResponse);
        }

        if (date != null) {
            Instant from = date.atStartOfDay(ZoneOffset.UTC).toInstant();
            Instant to = date.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
            log.debug("Listing appointments on date: {}", date);
            return appointmentRepository
                    .findByStartTimeBetween(from, to, pageable)
                    .map(appointmentMapper::toResponse);
        }

        log.debug("Listing all appointments - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        return appointmentRepository.findAll(pageable).map(appointmentMapper::toResponse);
    }

    public AppointmentResponse cancelAppointment(String id) {
        log.info("Cancelling appointment id: {}", id);

        Appointment appointment = getAppointmentEntityById(id);

        if (appointment.getStatus() != AppointmentStatus.BOOKED) {
            throw new BadRequestException(
                    "Only BOOKED appointments can be cancelled. Current status: " + appointment.getStatus());
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        Appointment saved = appointmentRepository.save(appointment);
        log.info("Appointment id: {} cancelled successfully.", id);

        notificationService.notifyAppointmentCancelled(saved);
        return appointmentMapper.toResponse(saved);
    }

    public AppointmentResponse updateAppointmentNotes(String id, UpdateAppointmentNotesRequest request) {
        log.debug("Updating notes for appointment id: {}", id);

        Appointment appointment = getAppointmentEntityById(id);
        appointment.setNotes(request.notes());

        Appointment saved = appointmentRepository.save(appointment);
        notificationService.notifyAppointmentUpdated(saved);
        return appointmentMapper.toResponse(saved);
    }

    private Appointment getAppointmentEntityById(String id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", id));
    }
}
