# MediNotify API

A Spring Boot 3 REST API for managing clinic patients, practitioners, appointments, and appointment notifications.

---

## Table of Contents

1. [Project Overview](#project-overview)
2. [Tech Stack](#tech-stack)
3. [Features](#features)
4. [Architecture](#architecture)
5. [OOP Design](#oop-design)
6. [Design Patterns Used](#design-patterns-used)
7. [Appointment Conflict Rule](#appointment-conflict-rule)
8. [API Endpoints](#api-endpoints)
9. [Sample Request Bodies](#sample-request-bodies)
10. [Error Handling](#error-handling)
11. [Running Locally](#running-locally)
12. [MongoDB Configuration](#mongodb-configuration)
13. [Postman Collection](#postman-collection)
14. [Running Tests](#running-tests)
15. [Assumptions and Trade-offs](#assumptions-and-trade-offs)
16. [Final Notes](#final-notes)

---

## Project Overview

MediNotify API is a backend REST service for managing core clinic operations:

- **Patients** — create and search patient records
- **Practitioners** — create and list doctors or clinical staff
- **Appointments** — schedule, cancel, update, and query appointments with conflict detection
- **Notifications** — triggered automatically on appointment booked, cancelled, and updated events

The project demonstrates the following engineering practices:

- Spring Boot REST API development
- MongoDB persistence with Spring Data
- DTO-based API design with clean separation from persistence models
- Jakarta Bean Validation on request inputs
- Global exception handling with a consistent error response format
- OOP composition for shared domain structures
- Strategy and Factory design patterns in the notification module
- Unit testing with JUnit 5 and Mockito

---

## Tech Stack

| Technology | Purpose |
|---|---|
| Java 17 | Language |
| Spring Boot 3.x | Application framework |
| Spring Web | REST API layer |
| Spring Data MongoDB | MongoDB persistence |
| MongoDB | Document database |
| Jakarta Bean Validation | Request validation |
| Maven | Build tool |
| Lombok | Boilerplate reduction |
| Springdoc OpenAPI / Swagger | API documentation |
| JUnit 5 | Unit testing |
| Mockito | Mocking framework |
| Docker Compose | Local MongoDB setup |

---

## Features

- Create and search patients by national ID or full name
- Create and list practitioners with pagination
- Create appointments with overlap conflict detection
- Get appointment by ID
- List appointments by practitioner and date with pagination
- Cancel appointments (BOOKED only)
- Update appointment notes
- Prevent overlapping BOOKED appointments for the same practitioner
- Trigger notifications for appointment booked, updated, and cancelled events
- Consistent error response format across all endpoints
- Swagger UI for interactive API exploration
- Dockerized MongoDB setup with authentication

---

## Architecture

The project follows a layered package structure:

```
com.poojithairosha.medinotifyapi
├── controller       # REST endpoints — thin, no business logic
├── service          # Business logic and orchestration
├── repository       # MongoDB access via Spring Data
├── dto
│   ├── request      # Inbound API contracts with validation annotations
│   └── response     # Outbound API contracts — no MongoDB types exposed
├── mapper           # Manual entity ↔ DTO conversion
├── model            # MongoDB documents and embedded value objects
├── exception        # Custom exceptions and global exception handler
├── notification     # Strategy/Factory-based notification module
└── config           # Application configuration
```

Each layer has a single responsibility. Controllers delegate entirely to services. Services depend on repositories and mappers, never on controllers. MongoDB model classes are never returned directly from any endpoint.

---

## OOP Design

The project uses **composition over inheritance** throughout the domain model.

```
Patient
└── PersonDetails
    ├── fullName
    └── ContactInfo
            ├── email
            └── phone

Practitioner
└── PersonDetails
    ├── fullName
    └── ContactInfo
            ├── email
            └── phone
```

`Patient` and `Practitioner` are separate MongoDB document roots (`@Document`). Both embed `PersonDetails`, which itself embeds `ContactInfo`. This allows shared person-related fields to be reused cleanly without coupling the two entities through a common superclass.

Since MongoDB stores document-based data, inheritance hierarchies add complexity without benefit. Composition keeps each document self-contained and independently queryable.

---

## Design Patterns Used

### Strategy Pattern + Factory Pattern — Notification Module

The notification module is built around two complementary patterns.

**Strategy Pattern**

`NotificationStrategy` is an interface with two methods:

```java
NotificationChannel getChannel();
void send(NotificationMessage message);
```

Three implementations provide the concrete strategies:

| Class | Channel |
|---|---|
| `ConsoleNotificationStrategy` | `CONSOLE` |
| `EmailNotificationStrategy` | `EMAIL` |
| `SmsNotificationStrategy` | `SMS` |

**Factory Pattern**

`NotificationStrategyFactory` accepts all `NotificationStrategy` implementations injected as a `List` by Spring, and builds a `Map<NotificationChannel, NotificationStrategy>` at construction time. Calling `getStrategy(channel)` returns the correct implementation or throws a `BadRequestException` for an unsupported channel.

**NotificationService**

`NotificationService` is the single entry point for the rest of the application. It constructs a `NotificationMessage` for each appointment event and delegates delivery through the factory:

- `notifyAppointmentBooked(Appointment)`
- `notifyAppointmentCancelled(Appointment)`
- `notifyAppointmentUpdated(Appointment)`

The application currently uses the `CONSOLE` channel for assignment scope. Adding a real email or SMS provider in future requires only a new `@Component` implementing `NotificationStrategy` — no changes to the factory or service are needed.

---

## Appointment Conflict Rule

A practitioner cannot have overlapping BOOKED appointments.

**Overlap formula:**

```
newStart < existingEnd  AND  newEnd > existingStart
```

The conflict check applies only when:
- The existing appointment belongs to the **same practitioner**
- The existing appointment has status **BOOKED**

Back-to-back appointments are explicitly allowed.

**Example:**

```
Existing:  10:00 ─────────── 11:00
New:                         11:00 ─────────── 12:00

Result: ALLOWED  (newStart == existingEnd, so newStart < existingEnd is false)
```

```
Existing:  10:00 ─────────── 11:00
New:                09:30 ─────────── 10:30

Result: CONFLICT  (09:30 < 11:00  AND  10:30 > 10:00)
```

---

## API Endpoints

### Patients

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/patients` | Create a patient |
| `GET` | `/api/patients/{id}` | Get patient by ID |
| `GET` | `/api/patients?nationalId=...` | Search by national ID |
| `GET` | `/api/patients?name=...` | Search by full name (partial, case-insensitive) |
| `GET` | `/api/patients?nationalId=...&name=...` | Search by both |

Supports `page`, `size`, and `sort` query parameters.

### Practitioners

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/practitioners` | Create a practitioner |
| `GET` | `/api/practitioners/{id}` | Get practitioner by ID |
| `GET` | `/api/practitioners` | List practitioners (paginated) |

Supports `registrationNo`, `name`, `page`, `size`, and `sort` query parameters.

### Appointments

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/appointments` | Create an appointment |
| `GET` | `/api/appointments/{id}` | Get appointment by ID |
| `GET` | `/api/appointments?practitionerId=...&date=YYYY-MM-DD` | List by practitioner and/or date (paginated) |
| `PATCH` | `/api/appointments/{id}/cancel` | Cancel a BOOKED appointment |
| `PATCH` | `/api/appointments/{id}` | Update appointment notes |

---

## Sample Request Bodies

### Create Patient

```json
POST /api/patients
{
  "fullName": "Amara Silva",
  "nationalId": "NIC-1987-004521",
  "dateOfBirth": "1987-03-14",
  "contact": {
    "email": "amara.silva@example.com",
    "phone": "+94771234567"
  }
}
```

### Create Practitioner

```json
POST /api/practitioners
{
  "fullName": "Dr. Rohan Perera",
  "registrationNo": "SLMC-2010-00892",
  "specialty": "Cardiology",
  "contact": {
    "email": "rohan.perera@clinic.lk",
    "phone": "+94112345678"
  }
}
```

### Create Appointment

```json
POST /api/appointments
{
  "patientId": "664a1f2e3b4c5d6e7f800001",
  "practitionerId": "664a1f2e3b4c5d6e7f800002",
  "startTime": "2026-05-10T09:00:00Z",
  "endTime": "2026-05-10T09:30:00Z",
  "notes": "Follow-up after blood pressure screening."
}
```

### Update Appointment Notes

```json
PATCH /api/appointments/{id}
{
  "notes": "Patient reported improvement. Schedule review in two weeks."
}
```

---

## Error Handling

All errors return a consistent JSON structure:

```json
{
  "timestamp": "2026-04-24T10:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "details": [
    "fullName: Full name must be at least 3 characters"
  ],
  "path": "/api/patients"
}
```

The `details` field is included only when field-level validation errors are present.

| Status | Trigger |
|---|---|
| `400 Bad Request` | Validation failure, invalid time range, invalid status operation |
| `404 Not Found` | Patient, practitioner, or appointment not found |
| `409 Conflict` | Duplicate national ID / registration number, or overlapping appointment |
| `500 Internal Server Error` | Unhandled exceptions — safe message returned to client, full stack trace logged |

---

## Running Locally

### Prerequisites

- Java 17+
- Maven
- Docker

### Steps

**1. Start MongoDB**

```bash
docker compose up -d
```

**2. Build the project**

```bash
mvn clean install
```

**3. Run the application**

```bash
mvn spring-boot:run
```

The application starts on:

```
http://localhost:8080
```

Swagger UI is available at:

```
http://localhost:8080/swagger-ui/index.html
```

---

## MongoDB Configuration

MongoDB runs through Docker Compose with authentication enabled.

The default connection string used by the application:

```
mongodb://medinotify_admin:medinotify_pass@localhost:27017/medinotify?authSource=admin
```

To override the connection string without modifying `application.yaml`, set the environment variable:

```bash
export MONGODB_URI=mongodb://<user>:<password>@<host>:<port>/<database>?authSource=admin
```

---

## Postman Collection

A Postman collection covering all endpoints is included in the project root:

```
docs/postman/API.postman_collection.json
```

Import the file into Postman to explore and test the API without writing requests manually.

---

## Running Tests

```bash
mvn test
```

Appointment service tests cover:

- Successful appointment creation when no conflict exists
- `ConflictException` thrown when appointments overlap
- Back-to-back appointments are correctly allowed
- Cancelling a BOOKED appointment sets status to CANCELLED
- Attempting to cancel a non-BOOKED appointment throws `BadRequestException`

---

## Assumptions and Trade-offs

| Area | Decision |
|---|---|
| Notifications | Console-based for this assignment scope. Email and SMS strategies are structural placeholders ready for a real provider integration. |
| Authentication | Not implemented. The assignment focused on appointment scheduling and notification behavior. |
| MongoDB auth | Authentication is enabled in the local Docker setup to reflect a realistic configuration. |
| API design | DTOs are used throughout. MongoDB model classes are never returned directly from any endpoint. |
| Notification timing | Notifications are sent synchronously after a successful save. Async delivery (e.g. via an event queue) would be the next step in production. |

---

## Final Notes

MediNotify API was built with a focus on clean architecture, maintainable layering, validated inputs, consistent error handling, and testable business logic. The design keeps each component responsible for one concern, making the codebase straightforward to extend — whether that means adding a new notification channel, a new search filter, or a new domain entity.
