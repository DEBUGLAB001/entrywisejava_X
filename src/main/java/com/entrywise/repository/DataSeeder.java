package com.entrywise.repository;

import com.entrywise.model.*;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

@Component
public class DataSeeder {
    private final EventRepository eventRepository;
    private final AttendeeRepository attendeeRepository;
    private final TicketRepository ticketRepository;
    private final SeatRepository seatRepository;
    private final EntryGateRepository gateRepository;
    private final QueueRepository queueRepository;
    private final CheckInRepository checkInRepository;

    public DataSeeder(EventRepository eventRepository,
                      AttendeeRepository attendeeRepository,
                      TicketRepository ticketRepository,
                      SeatRepository seatRepository,
                      EntryGateRepository gateRepository,
                      QueueRepository queueRepository,
                      CheckInRepository checkInRepository) {
        this.eventRepository = eventRepository;
        this.attendeeRepository = attendeeRepository;
        this.ticketRepository = ticketRepository;
        this.seatRepository = seatRepository;
        this.gateRepository = gateRepository;
        this.queueRepository = queueRepository;
        this.checkInRepository = checkInRepository;
    }

    @PostConstruct
    public void init() {
        resetData();
    }

    public synchronized void resetData() {
        eventRepository.clear();
        attendeeRepository.clear();
        ticketRepository.clear();
        seatRepository.clear();
        gateRepository.clear();
        queueRepository.clear();
        checkInRepository.clear();

        // 1. Seed Events
        LocalDateTime now = LocalDateTime.now();
        Event event1 = new Event(
                "EVT-101",
                "Summer Soundwave 2026",
                "Bayfront Grand Arena",
                now.plusDays(1),
                now.minusHours(2),
                now.plusHours(6),
                true,
                List.of(TicketCategory.VIP, TicketCategory.PREMIUM, TicketCategory.STANDARD),
                500,
                2, // baseline check-ins
                50, // reserved buffer capacity
                EventStatus.ADMISSION_OPEN
        );
        event1.setRemainingCapacity(498);
        eventRepository.save(event1);

        Event event2 = new Event(
                "EVT-102",
                "TechHorizon Summit 2026",
                "Metropolitan Expo Center",
                now.plusDays(3),
                now.minusHours(1),
                now.plusHours(8),
                false,
                List.of(TicketCategory.GENERAL_ADMISSION),
                800,
                0,
                80,
                EventStatus.ADMISSION_OPEN
        );
        event2.setRemainingCapacity(800);
        eventRepository.save(event2);

        // 2. Seed Entry Gates
        // Gate 1: Main Gate - unrestricted (empty allowedCategories set = all valid categories allowed)
        EntryGate gate1 = new EntryGate(
                "GATE-01",
                "Gate 1 — Main North Plaza",
                "EVT-101",
                GateStatus.OPEN,
                new HashSet<>(), // Unrestricted
                1
        );
        gateRepository.save(gate1);

        // Gate 2: VIP & FastTrack - restricted to VIP & PREMIUM
        EntryGate gate2 = new EntryGate(
                "GATE-02",
                "Gate 2 — VIP & FastTrack",
                "EVT-101",
                GateStatus.OPEN,
                Set.of(TicketCategory.VIP, TicketCategory.PREMIUM),
                1
        );
        gateRepository.save(gate2);

        // Gate 3: West General - restricted to STANDARD
        EntryGate gate3 = new EntryGate(
                "GATE-03",
                "Gate 3 — West General Entrance",
                "EVT-101",
                GateStatus.OPEN,
                Set.of(TicketCategory.STANDARD),
                0
        );
        gateRepository.save(gate3);

        // Gate 4 for Event 2
        EntryGate gate4 = new EntryGate(
                "GATE-04",
                "Expo Hall A Entrance",
                "EVT-102",
                GateStatus.OPEN,
                Set.of(TicketCategory.GENERAL_ADMISSION),
                0
        );
        gateRepository.save(gate4);

        // 3. Seed Attendees & Tickets
        // Attendee 1: Elena Rostova (VIP, RESERVED seat A-01, PENDING)
        Attendee att1 = new Attendee("ATT-501", "Elena Rostova", "elena.r@soundwave.io", "EVT-101", "TCK-1001", "A-01", AdmissionStatus.PENDING);
        attendeeRepository.save(att1);
        Ticket tck1 = new Ticket("TCK-1001", "EW-1001-VIP", "EVT-101", "ATT-501", TicketCategory.VIP, "SEAT-A01", TicketStatus.ACTIVE, AdmissionStatus.PENDING, null, null);
        ticketRepository.save(tck1);

        // Attendee 2: Liam Chen (VIP, OCCUPIED seat A-04, CHECKED_IN)
        Attendee att2 = new Attendee("ATT-502", "Liam Chen", "liam.chen@soundwave.io", "EVT-101", "TCK-1002", "A-04", AdmissionStatus.CHECKED_IN);
        attendeeRepository.save(att2);
        Ticket tck2 = new Ticket("TCK-1002", "EW-1002-VIP", "EVT-101", "ATT-502", TicketCategory.VIP, "SEAT-A04", TicketStatus.ACTIVE, AdmissionStatus.CHECKED_IN, now.minusMinutes(45), "GATE-02");
        ticketRepository.save(tck2);

        // Attendee 3: Chloe Bennett (PREMIUM, OCCUPIED seat B-01, CHECKED_IN)
        Attendee att3 = new Attendee("ATT-503", "Chloe Bennett", "chloe.b@soundwave.io", "EVT-101", "TCK-1003", "B-01", AdmissionStatus.CHECKED_IN);
        attendeeRepository.save(att3);
        Ticket tck3 = new Ticket("TCK-1003", "EW-1003-PREM", "EVT-101", "ATT-503", TicketCategory.PREMIUM, "SEAT-B01", TicketStatus.ACTIVE, AdmissionStatus.CHECKED_IN, now.minusMinutes(30), "GATE-01");
        ticketRepository.save(tck3);

        // Attendee 4: David Miller (STANDARD, PENDING, in queue seq 1)
        Attendee att4 = new Attendee("ATT-504", "David Miller", "david.m@soundwave.io", "EVT-101", "TCK-1004", null, AdmissionStatus.PENDING);
        attendeeRepository.save(att4);
        Ticket tck4 = new Ticket("TCK-1004", "EW-1004-STD", "EVT-101", "ATT-504", TicketCategory.STANDARD, null, TicketStatus.ACTIVE, AdmissionStatus.PENDING, null, null);
        ticketRepository.save(tck4);

        // Attendee 5: Sarah Jenkins (STANDARD, PENDING, in queue seq 2)
        Attendee att5 = new Attendee("ATT-505", "Sarah Jenkins", "sarah.j@soundwave.io", "EVT-101", "TCK-1005", null, AdmissionStatus.PENDING);
        attendeeRepository.save(att5);
        Ticket tck5 = new Ticket("TCK-1005", "EW-1005-STD", "EVT-101", "ATT-505", TicketCategory.STANDARD, null, TicketStatus.ACTIVE, AdmissionStatus.PENDING, null, null);
        ticketRepository.save(tck5);

        // Attendee 6: Marcus Vance (VIP, PENDING, in queue seq 3)
        Attendee att6 = new Attendee("ATT-506", "Marcus Vance", "marcus.v@soundwave.io", "EVT-101", "TCK-1006", null, AdmissionStatus.PENDING);
        attendeeRepository.save(att6);
        Ticket tck6 = new Ticket("TCK-1006", "EW-1006-VIP", "EVT-101", "ATT-506", TicketCategory.VIP, null, TicketStatus.ACTIVE, AdmissionStatus.PENDING, null, null);
        ticketRepository.save(tck6);

        // Attendee 7: Noah Wilson (Cancelled ticket)
        Attendee att7 = new Attendee("ATT-507", "Noah Wilson", "noah.w@soundwave.io", "EVT-101", "TCK-1007", null, AdmissionStatus.REJECTED);
        attendeeRepository.save(att7);
        Ticket tck7 = new Ticket("TCK-1007", "EW-9999-CAN", "EVT-101", "ATT-507", TicketCategory.STANDARD, null, TicketStatus.CANCELLED, AdmissionStatus.REJECTED, null, null);
        ticketRepository.save(tck7);

        // Attendee 8: Olivia Reed (Suspended ticket)
        Attendee att8 = new Attendee("ATT-508", "Olivia Reed", "olivia.r@soundwave.io", "EVT-101", "TCK-1008", null, AdmissionStatus.PENDING);
        attendeeRepository.save(att8);
        Ticket tck8 = new Ticket("TCK-1008", "EW-8888-SUS", "EVT-101", "ATT-508", TicketCategory.STANDARD, null, TicketStatus.SUSPENDED, AdmissionStatus.PENDING, null, null);
        ticketRepository.save(tck8);

        // Attendee 9: Sophia Turner (General Admission for Event 2)
        Attendee att9 = new Attendee("ATT-509", "Sophia Turner", "sophia.t@techhorizon.io", "EVT-102", "TCK-2001", null, AdmissionStatus.PENDING);
        attendeeRepository.save(att9);
        Ticket tck9 = new Ticket("TCK-2001", "EW-2001-GA", "EVT-102", "ATT-509", TicketCategory.GENERAL_ADMISSION, null, TicketStatus.ACTIVE, AdmissionStatus.PENDING, null, null);
        ticketRepository.save(tck9);

        // 4. Seed Seating Layout for EVT-101
        // Row A: VIP (A-01: RESERVED, A-02: AVAILABLE, A-03: AVAILABLE, A-04: OCCUPIED)
        seatRepository.save(new Seat("SEAT-A01", "EVT-101", "VIP Stage Front", "A", 1, TicketCategory.VIP, SeatStatus.RESERVED, "TCK-1001", "Elena Rostova"));
        seatRepository.save(new Seat("SEAT-A02", "EVT-101", "VIP Stage Front", "A", 2, TicketCategory.VIP, SeatStatus.AVAILABLE, null, null));
        seatRepository.save(new Seat("SEAT-A03", "EVT-101", "VIP Stage Front", "A", 3, TicketCategory.VIP, SeatStatus.AVAILABLE, null, null));
        seatRepository.save(new Seat("SEAT-A04", "EVT-101", "VIP Stage Front", "A", 4, TicketCategory.VIP, SeatStatus.OCCUPIED, "TCK-1002", "Liam Chen"));

        // Row B: PREMIUM (B-01: OCCUPIED, B-02..B-04: AVAILABLE)
        seatRepository.save(new Seat("SEAT-B01", "EVT-101", "Center Balcony", "B", 1, TicketCategory.PREMIUM, SeatStatus.OCCUPIED, "TCK-1003", "Chloe Bennett"));
        seatRepository.save(new Seat("SEAT-B02", "EVT-101", "Center Balcony", "B", 2, TicketCategory.PREMIUM, SeatStatus.AVAILABLE, null, null));
        seatRepository.save(new Seat("SEAT-B03", "EVT-101", "Center Balcony", "B", 3, TicketCategory.PREMIUM, SeatStatus.AVAILABLE, null, null));
        seatRepository.save(new Seat("SEAT-B04", "EVT-101", "Center Balcony", "B", 4, TicketCategory.PREMIUM, SeatStatus.AVAILABLE, null, null));

        // Row C: STANDARD (C-01..C-06)
        seatRepository.save(new Seat("SEAT-C01", "EVT-101", "Standard Tier", "C", 1, TicketCategory.STANDARD, SeatStatus.AVAILABLE, null, null));
        seatRepository.save(new Seat("SEAT-C02", "EVT-101", "Standard Tier", "C", 2, TicketCategory.STANDARD, SeatStatus.AVAILABLE, null, null));
        seatRepository.save(new Seat("SEAT-C03", "EVT-101", "Standard Tier", "C", 3, TicketCategory.STANDARD, SeatStatus.AVAILABLE, null, null));
        seatRepository.save(new Seat("SEAT-C04", "EVT-101", "Standard Tier", "C", 4, TicketCategory.STANDARD, SeatStatus.AVAILABLE, null, null));
        seatRepository.save(new Seat("SEAT-C05", "EVT-101", "Standard Tier", "C", 5, TicketCategory.STANDARD, SeatStatus.AVAILABLE, null, null));
        seatRepository.save(new Seat("SEAT-C06", "EVT-101", "Standard Tier", "C", 6, TicketCategory.STANDARD, SeatStatus.AVAILABLE, null, null));

        // 5. Seed Queue at Gate 1 and Gate 3
        QueueEntry q1 = new QueueEntry(
                "QUE-101", "EVT-101", "GATE-01", "TCK-1004", "EW-1004-STD", "David Miller",
                PriorityTier.STANDARD, 1L, now.minusMinutes(15), QueueStatus.WAITING, 8
        );
        queueRepository.save(q1);

        QueueEntry q2 = new QueueEntry(
                "QUE-102", "EVT-101", "GATE-01", "TCK-1005", "EW-1005-STD", "Sarah Jenkins",
                PriorityTier.STANDARD, 2L, now.minusMinutes(10), QueueStatus.WAITING, 12
        );
        queueRepository.save(q2);

        QueueEntry q3 = new QueueEntry(
                "QUE-103", "EVT-101", "GATE-01", "TCK-1006", "EW-1006-VIP", "Marcus Vance",
                PriorityTier.VIP, 3L, now.minusMinutes(5), QueueStatus.WAITING, 3
        );
        queueRepository.save(q3);

        QueueEntry q4 = new QueueEntry(
                "QUE-301", "EVT-101", "GATE-03", "TCK-1004", "EW-1004-STD", "David Miller",
                PriorityTier.STANDARD, 1L, now.minusMinutes(12), QueueStatus.WAITING, 6
        );
        queueRepository.save(q4);

        QueueEntry q5 = new QueueEntry(
                "QUE-302", "EVT-101", "GATE-03", "TCK-1005", "EW-1005-STD", "Sarah Jenkins",
                PriorityTier.STANDARD, 2L, now.minusMinutes(8), QueueStatus.WAITING, 10
        );
        queueRepository.save(q5);

        // 6. Seed Check-In Records (Initial 2 successful admissions)
        CheckInRecord r1 = new CheckInRecord(
                "REC-7001", "EVT-101", "TCK-1002", "EW-1002-VIP", "Liam Chen",
                "GATE-02", AdmissionOutcome.ADMITTED, now.minusMinutes(45), null
        );
        checkInRepository.save(r1);

        CheckInRecord r2 = new CheckInRecord(
                "REC-7002", "EVT-101", "TCK-1003", "EW-1003-PREM", "Chloe Bennett",
                "GATE-01", AdmissionOutcome.ADMITTED, now.minusMinutes(30), null
        );
        checkInRepository.save(r2);
    }
}
