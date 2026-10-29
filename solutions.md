# Solutions & Intentional Defect Catalog — EntryWise

> **CONFIDENTIAL — INTERNAL USE ONLY**  
> This document contains the master answer key and reference implementations for the **EntryWise** challenge.  
> It must be strictly protected against candidate access and excluded from public distributions, client-accessible directories, and test bundles.

---

## Challenge Summary

- **Challenge ID**: `entrywise-java-challenge`
- **Application**: EntryWise — Intelligent Event Entry & Crowd Management Platform
- **Technology Stack**: Java 17/18, Spring Boot 3.2.5, Maven, Vanilla JavaScript, CSS3
- **Primary Domain Services**:
  - `EntryQueueService` (`com.entrywise.service.EntryQueueService`)
  - `SeatAllocationService` (`com.entrywise.service.SeatAllocationService`)
  - `TicketCheckInService` (`com.entrywise.service.TicketCheckInService`)
  - `AdmissionCapacityService` (`com.entrywise.service.AdmissionCapacityService`)

---

## Intentional Defect Inventory

| Bug ID | Title | Category | Severity | Primary Affected File |
| :--- | :--- | :--- | :--- | :--- |
| **BUG-01** | Queue Priority Arrival Order Reversal | DSA / Priority Queue | High | `src/main/java/com/entrywise/service/EntryQueueService.java` |
| **BUG-02** | Candidate Seat Selection Assigns Reserved Seats | Algorithm / Filtering | High | `src/main/java/com/entrywise/service/SeatAllocationService.java` |
| **BUG-03** | Rejection of Valid Tickets at Unrestricted Gates | Business Logic / Validation | High | `src/main/java/com/entrywise/service/TicketCheckInService.java` |
| **BUG-04** | Remaining Capacity Neglects Admitted Attendees | State Management / Telemetry | Medium | `src/main/java/com/entrywise/service/AdmissionCapacityService.java` |

---

### BUG-01: Queue Priority Arrival Order Reversal

#### A. Bug ID and Title
- **ID**: BUG-01
- **Title**: Queue Priority Arrival Order Reversal

#### B. Bug Category
- **Category**: Data Structures & Algorithms (Priority Queue Comparator Ordering)

#### C. Difficulty / Severity
- **Severity**: High (Violates fair queuing and admission ordering)

#### D. Exact Location
- **File**: `src/main/java/com/entrywise/service/EntryQueueService.java`
- **Class**: `com.entrywise.service.EntryQueueService`
- **Method**: `getQueuePriorityComparator()`
- **Baseline Lines**: Lines 27–38 (specifically line 36)

#### E. Original Buggy Code
```java
    public Comparator<QueueEntry> getQueuePriorityComparator() {
        return (e1, e2) -> {
            int priorityComparison = Integer.compare(
                    e1.getPriorityTier().getLevel(),
                    e2.getPriorityTier().getLevel()
            );
            if (priorityComparison != 0) {
                return priorityComparison;
            }
            return Long.compare(e2.getSequenceNumber(), e1.getSequenceNumber());
        };
    }
```

#### F. Root Cause
- **Current Behavior**: When two attendees belong to the same priority tier (`priorityComparison == 0`), the comparator returns `Long.compare(e2.getSequenceNumber(), e1.getSequenceNumber())`.
- **Expected Behavior**: Attendees within the same priority tier must be scheduled in strict First-In, First-Out (FIFO) arrival order by their sequence number.
- **Why It Fails**: Comparing `e2` to `e1` reverses the natural sort order, producing Last-In, First-Out (LIFO) scheduling. Later arrivals receive larger sequence numbers and cut ahead of earlier arrivals.

#### G. Corrected Code
```java
    public Comparator<QueueEntry> getQueuePriorityComparator() {
        return (e1, e2) -> {
            int priorityComparison = Integer.compare(
                    e1.getPriorityTier().getLevel(),
                    e2.getPriorityTier().getLevel()
            );
            if (priorityComparison != 0) {
                return priorityComparison;
            }
            return Long.compare(e1.getSequenceNumber(), e2.getSequenceNumber());
        };
    }
```

#### H. Explanation
- In a priority queue, higher priority tiers (lower numerical level) are served first. When tiers match, fair queuing dictates that the attendee who entered first (smaller `sequenceNumber`) must be served first.
- Replacing `Long.compare(e2.getSequenceNumber(), e1.getSequenceNumber())` with `Long.compare(e1.getSequenceNumber(), e2.getSequenceNumber())` restores natural ascending sequence order, guaranteeing FIFO fairness.
- **Complexity**: Comparator is $O(1)$ time and $O(1)$ space. Sorting $N$ queue entries is $O(N \log N)$.

#### I. Verification
- **Test Command**: `java -cp target/classes com.entrywise.TestRunner`
- **Associated Test**: `test_entry_queue_priority_ordering`
- **Input Scenario**: Enqueue attendee A (sequence 101, standard tier) followed by attendee B (sequence 102, standard tier) at `GATE-01`.
- **Expected Result**: `processNextAttendee("GATE-01")` returns attendee A (`QUE-101`), not attendee B.

#### J. Related Bugs
- Independent of BUG-02, BUG-03, and BUG-04.

---

### BUG-02: Candidate Seat Selection Assigns Reserved Seats

#### A. Bug ID and Title
- **ID**: BUG-02
- **Title**: Candidate Seat Selection Assigns Reserved Seats

#### B. Bug Category
- **Category**: Algorithm / Candidate Filtering

#### C. Difficulty / Severity
- **Severity**: High (Causes double-booking conflicts and invalid seating)

#### D. Exact Location
- **File**: `src/main/java/com/entrywise/service/SeatAllocationService.java`
- **Class**: `com.entrywise.service.SeatAllocationService`
- **Method**: `findBestAvailableSeat(String eventId, TicketCategory category)`
- **Baseline Lines**: Lines 34–39 (specifically line 36)

#### E. Original Buggy Code
```java
        List<Seat> candidates = seatRepository.findByEventId(eventId).stream()
                .filter(s -> s.getCategory() == category)
                .filter(s -> s.getStatus() != SeatStatus.OCCUPIED)
                .sorted(Comparator.comparing(Seat::getRow).thenComparingInt(Seat::getSeatNumber))
                .collect(Collectors.toList());
```

#### F. Root Cause
- **Current Behavior**: The filter predicate `s.getStatus() != SeatStatus.OCCUPIED` excludes occupied seats, but includes both `SeatStatus.AVAILABLE` and `SeatStatus.RESERVED`.
- **Expected Behavior**: Automated seat allocation must only pick seats that are completely unreserved and available (`SeatStatus.AVAILABLE`).
- **Why It Fails**: Seats marked `SeatStatus.RESERVED` are earmarked for specific guests, VIP holds, or pending assignments. By allowing non-occupied seats, the allocator assigns reserved seats to general applicants, creating double-booking collisions.

#### G. Corrected Code
```java
        List<Seat> candidates = seatRepository.findByEventId(eventId).stream()
                .filter(s -> s.getCategory() == category)
                .filter(s -> s.getStatus() == SeatStatus.AVAILABLE)
                .sorted(Comparator.comparing(Seat::getRow).thenComparingInt(Seat::getSeatNumber))
                .collect(Collectors.toList());
```

#### H. Explanation
- Seats follow a three-state model: `AVAILABLE`, `RESERVED`, and `OCCUPIED`.
- Strict equality against `SeatStatus.AVAILABLE` ensures candidate selection never encroaches upon pre-allocated or reserved inventory.

#### I. Verification
- **Test Command**: `java -cp target/classes com.entrywise.TestRunner`
- **Associated Test**: `test_available_seat_selection_integrity`
- **Input Scenario**: Event `EVT-101` has VIP seat `SEAT-A01` (`RESERVED`) and `SEAT-A02` (`AVAILABLE`). Request allocation for VIP tier.
- **Expected Result**: Allocates `SEAT-A02`. `SEAT-A01` remains untouched in `RESERVED` state.

#### J. Related Bugs
- Independent of BUG-01, BUG-03, and BUG-04.

---

### BUG-03: Rejection of Valid Tickets at Unrestricted Gates

#### A. Bug ID and Title
- **ID**: BUG-03
- **Title**: Rejection of Valid Tickets at Unrestricted Gates

#### B. Bug Category
- **Category**: Business Logic / Validation

#### C. Difficulty / Severity
- **Severity**: High (Blocks valid attendees at general entry gates)

#### D. Exact Location
- **File**: `src/main/java/com/entrywise/service/TicketCheckInService.java`
- **Class**: `com.entrywise.service.TicketCheckInService`
- **Method**: `checkIn(String ticketCode, String eventId, String gateId)`
- **Baseline Lines**: Lines 89–100 (specifically line 92)

#### E. Original Buggy Code
```java
        Optional<EntryGate> gateOpt = gateRepository.findById(gateId);
        if (gateOpt.isPresent()) {
            EntryGate gate = gateOpt.get();
            if (gate.getAllowedCategories() != null && !gate.getAllowedCategories().contains(ticket.getCategory())) {
                CheckInRecord rejection = new CheckInRecord(
                        recordId, eventId, ticket.getId(), ticket.getTicketCode(), attendeeName,
                        gateId, AdmissionOutcome.REJECTED_GATE_RESTRICTION, now,
                        "Ticket category " + ticket.getCategory() + " is not permitted at gate " + gate.getName()
                );
                return checkInRepository.save(rejection);
            }
        }
```

#### F. Root Cause
- **Current Behavior**: Gates configured as universal/unrestricted have an empty set of allowed categories (`gate.getAllowedCategories().isEmpty() == true`).
- **Expected Behavior**: When `getAllowedCategories()` is empty, the gate is open to all valid ticket categories for that event.
- **Why It Fails**: Calling `!gate.getAllowedCategories().contains(...)` on an empty set always returns `true`, erroneously treating open gates as if all categories are forbidden and rejecting valid attendees with `REJECTED_GATE_RESTRICTION`.

#### G. Corrected Code
```java
        Optional<EntryGate> gateOpt = gateRepository.findById(gateId);
        if (gateOpt.isPresent()) {
            EntryGate gate = gateOpt.get();
            if (gate.getAllowedCategories() != null && !gate.getAllowedCategories().isEmpty() && !gate.getAllowedCategories().contains(ticket.getCategory())) {
                CheckInRecord rejection = new CheckInRecord(
                        recordId, eventId, ticket.getId(), ticket.getTicketCode(), attendeeName,
                        gateId, AdmissionOutcome.REJECTED_GATE_RESTRICTION, now,
                        "Ticket category " + ticket.getCategory() + " is not permitted at gate " + gate.getName()
                );
                return checkInRepository.save(rejection);
            }
        }
```

#### H. Explanation
- Adding `!gate.getAllowedCategories().isEmpty()` ensures that category restriction filtering is applied ONLY when restrictions have explicitly been configured on the turnstile. If the restriction set is empty, the check is skipped and admission proceeds.

#### I. Verification
- **Test Command**: `java -cp target/classes com.entrywise.TestRunner`
- **Associated Test**: `test_gate_category_admission_permission`
- **Input Scenario**: Scan valid ticket `EW-1001-VIP` at `GATE-01` (Main North Plaza, unrestricted).
- **Expected Result**: Record outcome is `AdmissionOutcome.ADMITTED`, not `REJECTED_GATE_RESTRICTION`.

#### J. Related Bugs
- If BUG-03 is fixed, successful check-ins will trigger `admissionCapacityService.syncCapacity(eventId)`, directly affecting BUG-04 telemetry.

---

### BUG-04: Remaining Capacity Neglects Admitted Attendees

#### A. Bug ID and Title
- **ID**: BUG-04
- **Title**: Remaining Capacity Neglects Admitted Attendees

#### B. Bug Category
- **Category**: State Management / Operational Telemetry

#### C. Difficulty / Severity
- **Severity**: Medium (Distorts venue capacity safety metrics)

#### D. Exact Location
- **File**: `src/main/java/com/entrywise/service/AdmissionCapacityService.java`
- **Class**: `com.entrywise.service.AdmissionCapacityService`
- **Method**: `getCapacityMetrics(String eventId)`
- **Baseline Lines**: Lines 34–40 (specifically line 37)

#### E. Original Buggy Code
```java
        int admitted = (int) successfulAdmissions;
        event.setAdmittedCount(admitted);

        int remaining = Math.max(0, event.getTotalCapacity() - event.getReservedBufferCapacity());
        event.setRemainingCapacity(remaining);
        eventRepository.save(event);
```

#### F. Root Cause
- **Current Behavior**: Remaining capacity is calculated solely as `totalCapacity - reservedBufferCapacity`.
- **Expected Behavior**: Available headroom must decrease as attendees enter the arena: `totalCapacity - reservedBufferCapacity - admitted`.
- **Why It Fails**: The subtraction omits `admitted`. Regardless of how many hundreds of attendees pass through turnstiles, the displayed remaining capacity remains static at `total - buffer`, misinforming gate security of available venue headroom.

#### G. Corrected Code
```java
        int admitted = (int) successfulAdmissions;
        event.setAdmittedCount(admitted);

        int remaining = Math.max(0, event.getTotalCapacity() - event.getReservedBufferCapacity() - admitted);
        event.setRemainingCapacity(remaining);
        eventRepository.save(event);
```

#### H. Explanation
- Subtracting `admitted` correctly accounts for human occupancy within the venue. `Math.max(0, ...)` guards against negative values in over-capacity situations.

#### I. Verification
- **Test Command**: `java -cp target/classes com.entrywise.TestRunner`
- **Associated Test**: `test_admission_capacity_synchronization`
- **Input Scenario**: Query capacity metrics for event with total capacity 500, buffer 0, and 3 admitted attendees.
- **Expected Result**: `remainingCapacity` equals `497` (500 - 0 - 3 = 497), not 500.

#### J. Related Bugs
- Connected to BUG-03: when BUG-03 is fixed and attendees are admitted, BUG-04 ensures remaining capacity decrements accurately.

