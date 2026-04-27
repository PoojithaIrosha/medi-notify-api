package com.poojithairosha.medinotifyapi.service;

import com.poojithairosha.medinotifyapi.dto.request.CreateAppointmentRequest;
import com.poojithairosha.medinotifyapi.dto.response.AppointmentResponse;
import com.poojithairosha.medinotifyapi.exception.BadRequestException;
import com.poojithairosha.medinotifyapi.exception.ConflictException;
import com.poojithairosha.medinotifyapi.mapper.AppointmentMapper;
import com.poojithairosha.medinotifyapi.model.Appointment;
import com.poojithairosha.medinotifyapi.model.AppointmentStatus;
import com.poojithairosha.medinotifyapi.notification.NotificationService;
import com.poojithairosha.medinotifyapi.repository.AppointmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private AppointmentMapper appointmentMapper;
    @Mock
    private PatientService patientService;
    @Mock
    private PractitionerService practitionerService;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private AppointmentService appointmentService;

    // ── fixtures ─────────────────────────────────────────────────────────────

    private static final String PATIENT_ID = "patient-001";
    private static final String PRACTITIONER_ID = "practitioner-001";
    private static final String APPOINTMENT_ID = "appointment-001";
    private static final Instant START_TIME = Instant.parse("2026-05-01T09:00:00Z");
    private static final Instant END_TIME = Instant.parse("2026-05-01T10:00:00Z");

    private CreateAppointmentRequest sampleCreateRequest() {
        return sampleCreateRequest(START_TIME, END_TIME);
    }

    private CreateAppointmentRequest sampleCreateRequest(Instant start, Instant end) {
        return new CreateAppointmentRequest(PATIENT_ID, PRACTITIONER_ID, start, end, null);
    }

    private Appointment sampleAppointment(AppointmentStatus status) {
        return Appointment.builder()
                .id(APPOINTMENT_ID)
                .patientId(PATIENT_ID)
                .practitionerId(PRACTITIONER_ID)
                .startTime(START_TIME)
                .endTime(END_TIME)
                .status(status)
                .build();
    }

    private AppointmentResponse sampleAppointmentResponse() {
        return new AppointmentResponse(
                APPOINTMENT_ID, PATIENT_ID, PRACTITIONER_ID,
                START_TIME, END_TIME, AppointmentStatus.BOOKED,
                null, Instant.now(), Instant.now()
        );
    }

    // ── createAppointment ────────────────────────────────────────────────────

    @Test
    void shouldCreateAppointmentWhenNoConflictExists() {
        CreateAppointmentRequest request = sampleCreateRequest();
        Appointment mapped = sampleAppointment(AppointmentStatus.BOOKED);
        Appointment saved = sampleAppointment(AppointmentStatus.BOOKED);
        AppointmentResponse response = sampleAppointmentResponse();

        doNothing().when(patientService).validatePatientExists(PATIENT_ID);
        doNothing().when(practitionerService).validatePractitionerExists(PRACTITIONER_ID);
        when(appointmentRepository.existsOverlappingAppointment(PRACTITIONER_ID, START_TIME, END_TIME))
                .thenReturn(false);
        when(appointmentMapper.toDocument(request)).thenReturn(mapped);
        when(appointmentRepository.save(mapped)).thenReturn(saved);
        when(appointmentMapper.toResponse(saved)).thenReturn(response);

        AppointmentResponse result = appointmentService.createAppointment(request);

        assertThat(result).isEqualTo(response);
        assertThat(mapped.getStatus()).isEqualTo(AppointmentStatus.BOOKED);
        verify(patientService).validatePatientExists(PATIENT_ID);
        verify(practitionerService).validatePractitionerExists(PRACTITIONER_ID);
        verify(appointmentRepository).save(mapped);
        verify(notificationService).notifyAppointmentBooked(saved);
    }

    @Test
    void shouldThrowConflictExceptionWhenAppointmentOverlaps() {
        CreateAppointmentRequest request = sampleCreateRequest();

        doNothing().when(patientService).validatePatientExists(PATIENT_ID);
        doNothing().when(practitionerService).validatePractitionerExists(PRACTITIONER_ID);
        when(appointmentRepository.existsOverlappingAppointment(PRACTITIONER_ID, START_TIME, END_TIME))
                .thenReturn(true);

        assertThatThrownBy(() -> appointmentService.createAppointment(request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("overlapping");

        verify(appointmentRepository, never()).save(any());
        verify(notificationService, never()).notifyAppointmentBooked(any());
    }

    @Test
    void shouldAllowBackToBackAppointments() {
        // Existing appointment ends at 11:00; new one starts at 11:00.
        // Overlap formula: newStart < existingEnd → 11:00 < 11:00 is false → no conflict.
        Instant backToBackStart = Instant.parse("2026-05-01T11:00:00Z");
        Instant backToBackEnd = Instant.parse("2026-05-01T12:00:00Z");
        CreateAppointmentRequest request = sampleCreateRequest(backToBackStart, backToBackEnd);

        Appointment mapped = sampleAppointment(AppointmentStatus.BOOKED);
        Appointment saved = sampleAppointment(AppointmentStatus.BOOKED);

        doNothing().when(patientService).validatePatientExists(PATIENT_ID);
        doNothing().when(practitionerService).validatePractitionerExists(PRACTITIONER_ID);
        when(appointmentRepository.existsOverlappingAppointment(PRACTITIONER_ID, backToBackStart, backToBackEnd))
                .thenReturn(false);
        when(appointmentMapper.toDocument(request)).thenReturn(mapped);
        when(appointmentRepository.save(mapped)).thenReturn(saved);
        when(appointmentMapper.toResponse(saved)).thenReturn(sampleAppointmentResponse());

        AppointmentResponse result = appointmentService.createAppointment(request);

        assertThat(result).isNotNull();
        // Service must delegate the back-to-back check to the repository, not short-circuit it
        verify(appointmentRepository).existsOverlappingAppointment(PRACTITIONER_ID, backToBackStart, backToBackEnd);
        verify(appointmentRepository).save(mapped);
    }

    // ── cancelAppointment ────────────────────────────────────────────────────

    @Test
    void shouldCancelBookedAppointment() {
        Appointment appointment = sampleAppointment(AppointmentStatus.BOOKED);
        Appointment saved = sampleAppointment(AppointmentStatus.CANCELLED);

        when(appointmentRepository.findById(APPOINTMENT_ID)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(saved);
        when(appointmentMapper.toResponse(saved)).thenReturn(sampleAppointmentResponse());

        appointmentService.cancelAppointment(APPOINTMENT_ID);

        // Verify the entity was mutated to CANCELLED before being persisted
        ArgumentCaptor<Appointment> captor = ArgumentCaptor.forClass(Appointment.class);
        verify(appointmentRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(AppointmentStatus.CANCELLED);

        verify(notificationService).notifyAppointmentCancelled(saved);
    }

    @ParameterizedTest
    @EnumSource(value = AppointmentStatus.class, names = {"CANCELLED", "COMPLETED"})
    void shouldThrowBadRequestWhenCancellingNonBookedAppointment(AppointmentStatus status) {
        Appointment appointment = sampleAppointment(status);
        when(appointmentRepository.findById(APPOINTMENT_ID)).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> appointmentService.cancelAppointment(APPOINTMENT_ID))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining(status.name());

        verify(appointmentRepository, never()).save(any());
        verify(notificationService, never()).notifyAppointmentCancelled(any());
    }
}
