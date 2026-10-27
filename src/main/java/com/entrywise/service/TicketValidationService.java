package com.entrywise.service;

import com.entrywise.model.*;
import com.entrywise.repository.*;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TicketValidationService {
    private final TicketRepository ticketRepository;
    private final AttendeeRepository attendeeRepository;
    private final SeatRepository seatRepository;
    private final EntryGateRepository gateRepository;

    public TicketValidationService(TicketRepository ticketRepository,
                                   AttendeeRepository attendeeRepository,
                                   SeatRepository seatRepository,
                                   EntryGateRepository gateRepository) {
        this.ticketRepository = ticketRepository;
        this.attendeeRepository = attendeeRepository;
        this.seatRepository = seatRepository;
        this.gateRepository = gateRepository;
    }

    public ValidationResult validateTicket(String ticketCode, String eventId, String gateId) {
        Optional<Ticket> ticketOpt = ticketRepository.findByCode(ticketCode);
        if (ticketOpt.isEmpty()) {
            ticketOpt = ticketRepository.findById(ticketCode);
        }

        if (ticketOpt.isEmpty()) {
            return new ValidationResult(false, "INVALID_CODE", "Ticket code not recognized in system",
                    null, null, null, null);
        }

        Ticket ticket = ticketOpt.get();
        Attendee attendee = attendeeRepository.findById(ticket.getAttendeeId()).orElse(null);
        Seat seat = ticket.getSeatId() != null ? seatRepository.findById(ticket.getSeatId()).orElse(null) : null;
        EntryGate gate = gateId != null ? gateRepository.findById(gateId).orElse(null) : null;

        if (eventId != null && !ticket.getEventId().equals(eventId)) {
            return new ValidationResult(false, "EVENT_MISMATCH", "Ticket is not registered for this event",
                    ticket, attendee, seat, gate);
        }

        if (ticket.getStatus() == TicketStatus.CANCELLED) {
            return new ValidationResult(false, "CANCELLED", "Ticket has been cancelled",
                    ticket, attendee, seat, gate);
        }

        if (ticket.getStatus() == TicketStatus.SUSPENDED) {
            return new ValidationResult(false, "SUSPENDED", "Ticket is suspended or pending verification",
                    ticket, attendee, seat, gate);
        }

        if (ticket.getAdmissionStatus() == AdmissionStatus.CHECKED_IN) {
            return new ValidationResult(false, "ALREADY_CHECKED_IN", "Ticket has already been admitted",
                    ticket, attendee, seat, gate);
        }

        return new ValidationResult(true, "VALID", "Ticket is valid for admission",
                ticket, attendee, seat, gate);
    }
}
