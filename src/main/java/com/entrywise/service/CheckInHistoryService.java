package com.entrywise.service;

import com.entrywise.model.*;
import com.entrywise.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CheckInHistoryService {
    private final CheckInRepository checkInRepository;
    private final TicketRepository ticketRepository;
    private final AttendeeRepository attendeeRepository;
    private final EntryGateRepository gateRepository;
    private final AdmissionCapacityService admissionCapacityService;

    public CheckInHistoryService(CheckInRepository checkInRepository,
                                 TicketRepository ticketRepository,
                                 AttendeeRepository attendeeRepository,
                                 EntryGateRepository gateRepository,
                                 AdmissionCapacityService admissionCapacityService) {
        this.checkInRepository = checkInRepository;
        this.ticketRepository = ticketRepository;
        this.attendeeRepository = attendeeRepository;
        this.gateRepository = gateRepository;
        this.admissionCapacityService = admissionCapacityService;
    }

    public List<CheckInRecord> getHistoryForEvent(String eventId) {
        return checkInRepository.findByEventId(eventId);
    }

    public List<CheckInRecord> getHistoryForGate(String gateId) {
        return checkInRepository.findByGateId(gateId);
    }

    public List<CheckInRecord> getAllHistory() {
        return checkInRepository.findAll();
    }

    public Optional<CheckInRecord> reverseAdmission(String recordId) {
        Optional<CheckInRecord> recordOpt = checkInRepository.findById(recordId);
        if (recordOpt.isEmpty()) {
            return Optional.empty();
        }

        CheckInRecord record = recordOpt.get();
        if (record.getOutcome() != AdmissionOutcome.ADMITTED) {
            throw new IllegalStateException("Only successful admissions can be reversed");
        }

        record.setOutcome(AdmissionOutcome.REJECTED_CANCELLED);
        record.setRejectionReason("Admission reversed by operations coordinator");
        checkInRepository.save(record);

        if (record.getTicketId() != null) {
            Optional<Ticket> ticketOpt = ticketRepository.findById(record.getTicketId());
            if (ticketOpt.isPresent()) {
                Ticket ticket = ticketOpt.get();
                ticket.setAdmissionStatus(AdmissionStatus.PENDING);
                ticket.setCheckInTimestamp(null);
                ticketRepository.save(ticket);

                Optional<Attendee> attendeeOpt = attendeeRepository.findById(ticket.getAttendeeId());
                if (attendeeOpt.isPresent()) {
                    Attendee attendee = attendeeOpt.get();
                    attendee.setAdmissionStatus(AdmissionStatus.PENDING);
                    attendeeRepository.save(attendee);
                }
            }
        }

        admissionCapacityService.syncCapacity(record.getEventId());

        return Optional.of(record);
    }
}
