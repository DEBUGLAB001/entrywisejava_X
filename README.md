# EntryWise — Intelligent Event Entry & Crowd Management Platform

EntryWise is an event entry and crowd management platform designed for concerts, conferences, and festivals. It enables gate operators to validate attendee tickets, automate seating assignments, manage prioritized turnstile queues, and monitor live venue occupancy through a real-time command dashboard.

## 1. Application Overview

### Core Functionality

- **Ticket Verification & Gate Check-In**: Scan attendee credentials to instantly verify ticket validity, event assignment, and gate permissions before authorizing admission.
- **Dynamic Seating Allocation**: Automatically allocate seats by category or manually view interactive seating maps with status indicators for available, reserved, and occupied seats.
- **Smart Queue Lane Management**: Organize incoming crowds across multiple gate turnstiles with priority lanes for VIP attendees and fair queuing for standard ticket holders.
- **Live Venue Telemetry**: Track real-time admissions, remaining venue headroom, turnstile queue lengths, and occupancy rates to maintain crowd safety.
- **Admission Audit Trail**: Review chronological access history logs with detailed entry outcomes, rejection reasons, and single-click admission reversals.

## 2. Debugging Challenge

While preparing EntryWise for an upcoming major festival, staff discovered several operational malfunctions that cause incorrect attendee queueing, accidental seat double-booking, unexpected gate rejections, and miscalculated venue occupancy counts. Your task is to diagnose and resolve each of these issues so event entry runs smoothly.

### Reported Issues & Tasks:

Issue 1: Queue Priority Arrival Order Reversal

- User Symptom: When multiple attendees with the same priority tier wait in a gate queue, latecomers are admitted ahead of people who arrived earlier, violating the expected line order.
- Task: Ensure that attendees within the same priority tier are admitted in strict arrival order, so earlier arrivals are always processed before later arrivals.

Issue 2: Candidate Seat Selection Assigns Reserved Seats

- User Symptom: When the system assigns a seat to an attendee, it occasionally gives away seats that were already reserved for VIP guests, resulting in double-booked seating conflicts.
- Task: Ensure seat allocation selects only completely unreserved, available seats and never assigns seats marked as reserved.

Issue 3: Rejection of Valid Tickets at Unrestricted Gates

- User Symptom: Attendees holding valid tickets are turned away with an access restriction error when scanning at main gates that are meant to welcome all ticket categories.
- Task: Ensure that unrestricted gates permit entry to all valid ticket holders for the event.

Issue 4: Remaining Capacity Neglects Admitted Attendees

- User Symptom: The venue capacity dashboard shows an unchanged number of available admission slots even as attendees check in, failing to account for people already inside the arena.
- Task: Ensure remaining capacity decreases as attendees are admitted, accurately reflecting real-time available headroom after accounting for safety buffers.

## 3. Expected Behavior After Fixing Bugs

1. Attendees in identical queue tiers are admitted strictly in first-come, first-served order.
2. Automated seat assignment assigns only unreserved, available seats.
3. Unrestricted general gates welcome all valid event ticket tiers without false rejections.
4. Remaining capacity numbers decrease dynamically with every successful admission.


## Seed Data Reference

Use these seeded ticket identifiers and scenarios to test validation, gate routing, and seating workflows directly in the application:

| Ticket Code | Attendee Name | Ticket Tier | Assigned Event | Recommended Gate | Initial Status | Test Scenario / Purpose |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| `EW-1001-VIP` | Elena Rostova | VIP | EVT-101 | Gate 1 (Main North) | Active / Pending | Valid VIP ticket testing unrestricted gate access at Gate 1. |
| `EW-1002-VIP` | Liam Chen | VIP | EVT-101 | Gate 2 (VIP FastTrack) | Checked In | Duplicate entry verification; tests rejection of already admitted tickets. |
| `EW-1003-PREM` | Chloe Bennett | Premium | EVT-101 | Gate 1 (Main North) | Checked In | Duplicate entry verification for premium tier attendees. |
| `EW-1004-STD` | David Miller | Standard | EVT-101 | Gate 3 (West General) | Active / Pending | Standard ticket awaiting seat assignment; tests seat allocation integrity. |
| `EW-1005-STD` | Sarah Jenkins | Standard | EVT-101 | Gate 3 (West General) | Active / Pending | Position 2 in queue lane; tests FIFO queue progression against David Miller. |
| `EW-1006-VIP` | Marcus Vance | VIP | EVT-101 | Gate 1 (Main North) | Active / Pending | VIP ticket in queue sequence 3; tests priority lane jump over standard tiers. |
| `EW-9999-CAN` | Noah Wilson | Standard | EVT-101 | Gate 1 (Main North) | Cancelled | Ticket validity testing; verifies immediate rejection of cancelled credentials. |
| `EW-8888-SUS` | Olivia Reed | Standard | EVT-101 | Gate 1 (Main North) | Suspended | Security verification; verifies security rejection of suspended tickets. |
| `EW-2001-GA` | Sophia Turner | General Admission | EVT-102 | Gate 4 (Expo Hall A) | Active / Pending | Cross-event verification; tests event mismatch rejection when scanned at EVT-101. |
| `EW-UNKNOWN-99` | Unknown | Unknown | — | Gate 1 (Main North) | Invalid | Unknown barcode verification; tests 404 credential not found handling. |
