package com.entrywise.service;

import com.entrywise.model.*;
import com.entrywise.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class TicketCheckInService {
    private final TicketRepository ticketRepository;
    private final AttendeeRepository attendeeRepository;
    private final EntryGateRepository gateRepository;
    private final CheckInRepository checkInRepository;
    private final AdmissionCapacityService admissionCapacityService;

    public TicketCheckInService(TicketRepository ticketRepository,
                                AttendeeRepository attendeeRepository,
                                EntryGateRepository gateRepository,
                                CheckInRepository checkInRepository,
                                AdmissionCapacityService admissionCapacityService) {
        this.ticketRepository = ticketRepository;
        this.attendeeRepository = attendeeRepository;
        this.gateRepository = gateRepository;
        this.checkInRepository = checkInRepository;
        this.admissionCapacityService = admissionCapacityService;
    }

    public CheckInRecord checkIn(String ticketCode, String eventId, String gateId) {
        Optional<Ticket> ticketOpt = ticketRepository.findByCode(ticketCode);
        if (ticketOpt.isEmpty()) {
            ticketOpt = ticketRepository.findById(ticketCode);
        }

        LocalDateTime now = LocalDateTime.now();
        String recordId = "REC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        if (ticketOpt.isEmpty()) {
            CheckInRecord rejection = new CheckInRecord(
                    recordId, eventId, null, ticketCode, "Unknown",
                    gateId, AdmissionOutcome.REJECTED_INVALID_CODE, now,
                    "Ticket code was not recognized by the system"
            );
            return checkInRepository.save(rejection);
        }

        Ticket ticket = ticketOpt.get();
        Attendee attendee = attendeeRepository.findById(ticket.getAttendeeId()).orElse(null);
        String attendeeName = attendee != null ? attendee.getName() : "Attendee";

        if (eventId != null && !ticket.getEventId().equals(eventId)) {
            CheckInRecord rejection = new CheckInRecord(
                    recordId, eventId, ticket.getId(), ticket.getTicketCode(), attendeeName,
                    gateId, AdmissionOutcome.REJECTED_EVENT_MISMATCH, now,
                    "Ticket is not registered for this event"
            );
            return checkInRepository.save(rejection);
        }

        if (ticket.getStatus() == TicketStatus.CANCELLED) {
            CheckInRecord rejection = new CheckInRecord(
                    recordId, eventId, ticket.getId(), ticket.getTicketCode(), attendeeName,
                    gateId, AdmissionOutcome.REJECTED_CANCELLED, now,
                    "Ticket has been cancelled"
            );
            return checkInRepository.save(rejection);
        }

        if (ticket.getStatus() == TicketStatus.SUSPENDED) {
            CheckInRecord rejection = new CheckInRecord(
                    recordId, eventId, ticket.getId(), ticket.getTicketCode(), attendeeName,
                    gateId, AdmissionOutcome.REJECTED_SUSPENDED, now,
                    "Ticket is currently suspended"
            );
            return checkInRepository.save(rejection);
        }

        if (ticket.getAdmissionStatus() == AdmissionStatus.CHECKED_IN) {
            CheckInRecord rejection = new CheckInRecord(
                    recordId, eventId, ticket.getId(), ticket.getTicketCode(), attendeeName,
                    gateId, AdmissionOutcome.REJECTED_ALREADY_USED, now,
                    "Ticket was already checked in at " + ticket.getCheckInTimestamp()
            );
            return checkInRepository.save(rejection);
        }

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

        ticket.setAdmissionStatus(AdmissionStatus.CHECKED_IN);
        ticket.setCheckInTimestamp(now);
        ticket.setGateId(gateId);
        ticketRepository.save(ticket);

        if (attendee != null) {
            attendee.setAdmissionStatus(AdmissionStatus.CHECKED_IN);
            attendeeRepository.save(attendee);
        }

        if (gateOpt.isPresent()) {
            EntryGate gate = gateOpt.get();
            gate.setTotalAdmissions(gate.getTotalAdmissions() + 1);
            gateRepository.save(gate);
        }

        CheckInRecord admittedRecord = new CheckInRecord(
                recordId, eventId, ticket.getId(), ticket.getTicketCode(), attendeeName,
                gateId, AdmissionOutcome.ADMITTED, now, null
        );
        checkInRepository.save(admittedRecord);

        admissionCapacityService.syncCapacity(eventId);

        return admittedRecord;
    }
}
