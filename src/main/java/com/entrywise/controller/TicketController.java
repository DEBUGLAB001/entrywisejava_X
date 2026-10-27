package com.entrywise.controller;

import com.entrywise.dto.TicketValidationRequest;
import com.entrywise.model.Attendee;
import com.entrywise.model.Ticket;
import com.entrywise.model.ValidationResult;
import com.entrywise.repository.AttendeeRepository;
import com.entrywise.repository.TicketRepository;
import com.entrywise.service.TicketValidationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TicketController {
    private final TicketRepository ticketRepository;
    private final AttendeeRepository attendeeRepository;
    private final TicketValidationService validationService;

    public TicketController(TicketRepository ticketRepository,
                            AttendeeRepository attendeeRepository,
                            TicketValidationService validationService) {
        this.ticketRepository = ticketRepository;
        this.attendeeRepository = attendeeRepository;
        this.validationService = validationService;
    }

    @GetMapping("/tickets")
    public ResponseEntity<List<Ticket>> getTickets(@RequestParam(required = false) String eventId) {
        if (eventId != null && !eventId.isBlank()) {
            return ResponseEntity.ok(ticketRepository.findByEventId(eventId));
        }
        return ResponseEntity.ok(ticketRepository.findAll());
    }

    @GetMapping("/tickets/{code}")
    public ResponseEntity<Ticket> getTicketByCode(@PathVariable String code) {
        return ticketRepository.findByCode(code)
                .or(() -> ticketRepository.findById(code))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/tickets/validate")
    public ResponseEntity<ValidationResult> validateTicket(@RequestBody TicketValidationRequest request) {
        ValidationResult result = validationService.validateTicket(
                request.getTicketCode(),
                request.getEventId(),
                request.getGateId()
        );
        return ResponseEntity.ok(result);
    }

    @GetMapping("/attendees")
    public ResponseEntity<List<Attendee>> getAttendees(@RequestParam(required = false) String eventId) {
        if (eventId != null && !eventId.isBlank()) {
            return ResponseEntity.ok(attendeeRepository.findByEventId(eventId));
        }
        return ResponseEntity.ok(attendeeRepository.findAll());
    }
}
