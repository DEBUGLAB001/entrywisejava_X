# EntryWise

## Description
EntryWise is an event entry and crowd management platform designed for ticketing verification, admission queueing, seating layout allocation, and venue capacity monitoring. Built with Java 18 and Spring Boot 3.2.5, the application exposes REST endpoints consumed by a vanilla JavaScript and CSS web interface. The backend entry point is `com.entrywise.EntryWiseApplication` located in `src/main/java/com/entrywise/EntryWiseApplication.java`. Operations are coordinated across domain services including `TicketCheckInService` for credential validation, `EntryQueueService` for priority turnstile scheduling, `SeatAllocationService` for reserved and general seating assignment, and `AdmissionCapacityService` for real-time venue telemetry. Application state is held in synchronized in-memory repositories populated deterministically on startup via `DataSeeder` in `src/main/java/com/entrywise/repository/DataSeeder.java`.

## Repository Structure
```
.
├── challenge.json                                # DEBUG//LAB challenge metadata and test command
├── pom.xml                                       # Maven build configuration and Spring Boot dependencies
├── start.sh                                      # Application startup script
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── entrywise/
        │           ├── EntryWiseApplication.java # Spring Boot main entry point
        │           ├── controller/               # REST API controllers
        │           │   ├── CheckInController.java
        │           │   ├── EventController.java
        │           │   ├── GateController.java
        │           │   ├── OperationsController.java
        │           │   ├── QueueController.java
        │           │   ├── SeatingController.java
        │           │   └── TicketController.java
        │           ├── model/                    # Domain models and status enums
        │           │   ├── AdmissionOutcome.java
        │           │   ├── AdmissionStatus.java
        │           │   ├── Attendee.java
        │           │   ├── CapacityMetrics.java
        │           │   ├── CheckInRecord.java
        │           │   ├── EntryGate.java
        │           │   ├── Event.java
        │           │   ├── GateStatus.java
        │           │   ├── PriorityTier.java
        │           │   ├── QueueEntry.java
        │           │   ├── QueueStatus.java
        │           │   ├── Seat.java
        │           │   ├── SeatStatus.java
        │           │   ├── Ticket.java
        │           │   ├── TicketCategory.java
        │           │   ├── TicketStatus.java
        │           │   └── ValidationResult.java
        │           ├── repository/               # Concurrent in-memory storage & seed data
        │           │   ├── AttendeeRepository.java
        │           │   ├── CheckInRepository.java
        │           │   ├── DataSeeder.java
        │           │   ├── EntryGateRepository.java
        │           │   ├── EventRepository.java
        │           │   ├── QueueRepository.java
        │           │   ├── SeatRepository.java
        │           │   └── TicketRepository.java
        │           └── service/                  # Core business logic services
        │               ├── AdmissionCapacityService.java
        │               ├── EntryGateService.java
        │               ├── EntryQueueService.java
        │               ├── EventOperationsService.java
        │               ├── SeatAllocationService.java
        │               └── TicketCheckInService.java
        └── resources/
            ├── application.properties            # Server port 3000 configuration
            └── static/                           # Outseta-inspired frontend assets
                ├── css/
                │   └── style.css
                ├── js/
                │   └── app.js
                └── index.html
```

## Bugs and Bug Locations

1. Queue Priority Arrival Order Reversal

- Bug location: `src/main/java/com/entrywise/service/EntryQueueService.java`, within `processNextAttendee(String gateId)`.
- How to observe it: Enqueue two attendees in the same priority tier (e.g., standard attendees with sequence numbers 1 and 2 at `GATE-01`) and invoke `POST /api/queue/GATE-01/next`.
- Failure: The comparator orders identical priority tiers using `Long.compare(e2.getSequenceNumber(), e1.getSequenceNumber())` instead of ascending sequence, causing later arrivals to cut in front of earlier arrivals in last-in, first-out (LIFO) order.
- Expected: Attendees within the same priority tier must be scheduled in strict first-in, first-out (FIFO) order by arrival sequence (`Long.compare(e1.getSequenceNumber(), e2.getSequenceNumber())`).

2. Candidate Seat Selection Assigns Reserved Seats

- Bug location: `src/main/java/com/entrywise/service/SeatAllocationService.java`, within `allocateSeat(String eventId, TicketCategory category)`.
- How to observe it: Request automatic seat allocation for `EVT-101` and `TicketCategory.VIP` where `SEAT-A01` is marked `RESERVED` and `SEAT-A02` is `AVAILABLE`.
- Failure: The seat availability filter checks `s.getStatus() != SeatStatus.OCCUPIED`, incorrectly matching seats with `SeatStatus.RESERVED` and allocating pre-held seats to unreserved attendees.
- Expected: The allocation filter must only select unallocated seats whose status is strictly `SeatStatus.AVAILABLE`.

3. Rejection of Valid Tickets at Unrestricted Gates

- Bug location: `src/main/java/com/entrywise/service/TicketCheckInService.java`, within `validateTicket(String ticketCode, String eventId, String gateId)`.
- How to observe it: Validate active ticket `EW-1001-VIP` at `GATE-01` (Main North Plaza), which is configured with an empty set of allowed categories.
- Failure: The restriction check tests `!gate.getAllowedCategories().contains(ticket.getCategory())` without first verifying whether `gate.getAllowedCategories().isEmpty()`, rejecting valid attendees with `GATE_RESTRICTION` at gates designated for all attendee categories.
- Expected: Gates with an empty category restriction set must be treated as unrestricted and permit all valid ticket categories for the event.

4. Remaining Capacity Neglects Admitted Attendees

- Bug location: `src/main/java/com/entrywise/service/AdmissionCapacityService.java`, within `calculateMetrics(String eventId)`.
- How to observe it: Query capacity metrics via `GET /api/events/{eventId}/capacity` for `EVT-101` having total capacity 500, buffer capacity 50, and 2 admitted attendees.
- Failure: The service calculates remaining capacity as `totalCapacity - bufferCapacity` (`500 - 50 = 450`), neglecting to subtract `admittedCount` and displaying an inflated capacity of 450 instead of 448.
- Expected: Remaining capacity must equal `totalCapacity - bufferCapacity - admittedCount`, accurately decreasing as attendees are checked in.
