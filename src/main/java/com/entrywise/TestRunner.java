package com.entrywise;

import com.entrywise.model.*;
import com.entrywise.repository.*;
import com.entrywise.service.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

public class TestRunner {

    static class TestResult {
        String name;
        boolean passed;
        long durationMs;
        String errorMessage;

        TestResult(String name, boolean passed, long durationMs, String errorMessage) {
            this.name = name;
            this.passed = passed;
            this.durationMs = durationMs;
            this.errorMessage = errorMessage;
        }
    }

    public static void main(String[] args) {
        List<TestResult> results = new ArrayList<>();
        long totalStart = System.currentTimeMillis();

        results.add(runTest("test_entry_queue_priority_ordering", TestRunner::testEntryQueuePriorityOrdering));
        results.add(runTest("test_available_seat_selection_integrity", TestRunner::testAvailableSeatSelectionIntegrity));
        results.add(runTest("test_gate_category_admission_permission", TestRunner::testGateCategoryAdmissionPermission));
        results.add(runTest("test_admission_capacity_synchronization", TestRunner::testAdmissionCapacitySynchronization));

        long totalEnd = System.currentTimeMillis();
        long totalExecutionTime = totalEnd - totalStart;

        int passedCount = 0;
        int failedCount = 0;

        StringBuilder json = new StringBuilder();
        json.append("{\n");

        for (int i = 0; i < results.size(); i++) {
            TestResult r = results.get(i);
            if (r.passed) {
                passedCount++;
            } else {
                failedCount++;
            }

            json.append("  \"").append(r.name).append("\": {\n");
            json.append("    \"Status\": \"").append(r.passed ? "passed" : "failed").append("\",\n");
            json.append("    \"Execution time\": \"").append(r.durationMs).append("ms\"");
            if (!r.passed) {
                json.append(",\n    \"Error\": \"").append(escapeJson(r.errorMessage)).append("\"");
            }
            json.append("\n  },\n");
        }

        json.append("  \"Passed\": ").append(passedCount).append(",\n");
        json.append("  \"Failed\": ").append(failedCount).append(",\n");
        json.append("  \"Total bugs\": 4,\n");
        json.append("  \"Total Execution time\": \"").append(totalExecutionTime).append("ms\"\n");
        json.append("}");

        System.out.println(json.toString());

        if (failedCount > 0) {
            System.exit(1);
        } else {
            System.exit(0);
        }
    }

    private static TestResult runTest(String testName, Runnable test) {
        long start = System.currentTimeMillis();
        try {
            test.run();
            long duration = System.currentTimeMillis() - start;
            return new TestResult(testName, true, duration, null);
        } catch (Throwable t) {
            long duration = System.currentTimeMillis() - start;
            String msg = t.getMessage();
            if (msg == null || msg.isBlank()) {
                StringWriter sw = new StringWriter();
                t.printStackTrace(new PrintWriter(sw));
                msg = sw.toString();
            }
            return new TestResult(testName, false, duration, msg);
        }
    }

    private static String escapeJson(String raw) {
        if (raw == null) return "";
        return raw.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    static class TestContext {
        final EventRepository eventRepo = new EventRepository();
        final AttendeeRepository attendeeRepo = new AttendeeRepository();
        final TicketRepository ticketRepo = new TicketRepository();
        final SeatRepository seatRepo = new SeatRepository();
        final EntryGateRepository gateRepo = new EntryGateRepository();
        final QueueRepository queueRepo = new QueueRepository();
        final CheckInRepository checkInRepo = new CheckInRepository();

        final EventService eventService;
        final TicketValidationService validationService;
        final EntryQueueService queueService;
        final SeatAllocationService seatingService;
        final AdmissionCapacityService capacityService;
        final TicketCheckInService checkInService;
        final GateOperationsService gateService;
        final CheckInHistoryService historyService;
        final DataSeeder seeder;

        TestContext() {
            this.eventService = new EventService(eventRepo);
            this.validationService = new TicketValidationService(ticketRepo, attendeeRepo, seatRepo, gateRepo);
            this.queueService = new EntryQueueService(queueRepo, ticketRepo, gateRepo);
            this.seatingService = new SeatAllocationService(seatRepo, ticketRepo, eventRepo);
            this.capacityService = new AdmissionCapacityService(eventRepo, checkInRepo, gateRepo, queueRepo);
            this.checkInService = new TicketCheckInService(ticketRepo, attendeeRepo, gateRepo, checkInRepo, capacityService);
            this.gateService = new GateOperationsService(gateRepo);
            this.historyService = new CheckInHistoryService(checkInRepo, ticketRepo, attendeeRepo, gateRepo, capacityService);

            this.seeder = new DataSeeder(
                    eventRepo, attendeeRepo, ticketRepo,
                    seatRepo, gateRepo, queueRepo, checkInRepo
            );
            this.seeder.resetData();
        }
    }

    // Verify queue entry ordering and arrival priority tie-breaker
    private static void testEntryQueuePriorityOrdering() {
        TestContext ctx = new TestContext();
        // At GATE-03, David Miller (QUE-301, seq 1, STANDARD) arrived before Sarah Jenkins (QUE-302, seq 2, STANDARD)
        // Under FIFO tie-breaking for equal priority tier, QUE-301 must be processed before QUE-302
        Optional<QueueEntry> next = ctx.queueService.findNextEligibleEntry("GATE-03");
        if (next.isEmpty()) {
            throw new AssertionError("Expected eligible queue entry at GATE-03");
        }

        QueueEntry candidate = next.get();
        if (candidate.getSequenceNumber() != 1L || !"QUE-301".equals(candidate.getId())) {
            throw new AssertionError("Expected queue entry 'QUE-301' (sequence 1) to be next in FIFO order, but got '"
                    + candidate.getId() + "' (sequence " + candidate.getSequenceNumber() + ")");
        }
    }

    // Verify candidate seat filtering and allocation availability
    private static void testAvailableSeatSelectionIntegrity() {
        TestContext ctx = new TestContext();
        // For EVT-101, SEAT-A01 is VIP but RESERVED for Elena Rostova. SEAT-A02 is VIP and AVAILABLE.
        // Auto-selection for VIP category must return SEAT-A02 and never an already reserved or occupied seat.
        Optional<Seat> selected = ctx.seatingService.findBestAvailableSeat("EVT-101", TicketCategory.VIP);
        if (selected.isEmpty()) {
            throw new AssertionError("Expected an available VIP seat to be found");
        }

        Seat seat = selected.get();
        if (seat.getStatus() != SeatStatus.AVAILABLE) {
            throw new AssertionError("Candidate seat selection returned seat '" + seat.getId()
                    + "' with status " + seat.getStatus() + ", expected available seat 'SEAT-A02'");
        }

        if (!"SEAT-A02".equals(seat.getId())) {
            throw new AssertionError("Expected seat 'SEAT-A02' to be selected, but got '" + seat.getId() + "'");
        }
    }

    // Verify gate admission permissions for unrestricted entrance gates
    private static void testGateCategoryAdmissionPermission() {
        TestContext ctx = new TestContext();
        // Gate 1 (GATE-01) is the main entrance with unrestricted category access (empty allowedCategories set).
        // Attendee Elena Rostova with valid active ticket EW-1001-VIP must be admitted through GATE-01.
        CheckInRecord record = ctx.checkInService.checkIn("EW-1001-VIP", "EVT-101", "GATE-01");
        if (record.getOutcome() != AdmissionOutcome.ADMITTED) {
            throw new AssertionError("Expected ticket 'EW-1001-VIP' to be ADMITTED at unrestricted gate 'GATE-01', but received rejection: "
                    + record.getOutcome() + (record.getRejectionReason() != null ? " (" + record.getRejectionReason() + ")" : ""));
        }
    }

    // Verify admission capacity metrics synchronization with check-in records
    private static void testAdmissionCapacitySynchronization() {
        TestContext ctx = new TestContext();
        // EVT-101 has totalCapacity 500. Initial baseline check-ins = 2.
        // Admit attendee Marcus Vance (EW-1006-VIP) through GATE-02 (VIP gate).
        CheckInRecord record = ctx.checkInService.checkIn("EW-1006-VIP", "EVT-101", "GATE-02");
        if (record.getOutcome() != AdmissionOutcome.ADMITTED) {
            throw new AssertionError("Failed to admit ticket for capacity test: " + record.getOutcome());
        }

        // Capacity metrics must maintain invariant: totalCapacity == admittedCount + remainingCapacity
        CapacityMetrics metrics = ctx.capacityService.getCapacityMetrics("EVT-101");
        int expectedAdmitted = 3;
        int expectedRemaining = 500 - expectedAdmitted;

        if (metrics.getAdmittedCount() != expectedAdmitted) {
            throw new AssertionError("Expected admittedCount " + expectedAdmitted + ", but got " + metrics.getAdmittedCount());
        }

        if (metrics.getRemainingCapacity() != expectedRemaining) {
            throw new AssertionError("Expected remaining capacity " + expectedRemaining
                    + " (total 500 - admitted " + expectedAdmitted + "), but got " + metrics.getRemainingCapacity());
        }
    }
}
