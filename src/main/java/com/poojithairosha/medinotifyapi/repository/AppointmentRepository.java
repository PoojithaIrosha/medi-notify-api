package com.poojithairosha.medinotifyapi.repository;

import com.poojithairosha.medinotifyapi.model.Appointment;
import com.poojithairosha.medinotifyapi.model.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ExistsQuery;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;

public interface AppointmentRepository extends MongoRepository<Appointment, String> {

    // Overlap conflict check: newStart < existingEnd AND newEnd > existingStart
    @ExistsQuery("{ 'practitionerId': ?0, 'status': 'BOOKED', 'startTime': { $lt: ?2 }, 'endTime': { $gt: ?1 } }")
    boolean existsOverlappingAppointment(String practitionerId, Instant newStart, Instant newEnd);

    Page<Appointment> findByPractitionerId(String practitionerId, Pageable pageable);

    Page<Appointment> findByStartTimeBetween(Instant from, Instant to, Pageable pageable);

    Page<Appointment> findByPractitionerIdAndStartTimeBetween(
            String practitionerId, Instant from, Instant to, Pageable pageable);
}
