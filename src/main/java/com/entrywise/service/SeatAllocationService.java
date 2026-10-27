package com.entrywise.service;

import com.entrywise.model.*;
import com.entrywise.repository.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SeatAllocationService {
    private final SeatRepository seatRepository;
    private final TicketRepository ticketRepository;
    private final EventRepository eventRepository;

    public SeatAllocationService(SeatRepository seatRepository,
                                 TicketRepository ticketRepository,
                                 EventRepository eventRepository) {
        this.seatRepository = seatRepository;
        this.ticketRepository = ticketRepository;
        this.eventRepository = eventRepository;
    }

    public List<Seat> getSeatingLayout(String eventId) {
        return seatRepository.findByEventId(eventId);
    }

    public Optional<Seat> findBestAvailableSeat(String eventId, TicketCategory category) {
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        if (eventOpt.isEmpty() || !eventOpt.get().isAssignedSeating()) {
            return Optional.empty();
        }

        List<Seat> candidates = seatRepository.findByEventId(eventId).stream()
                .filter(s -> s.getCategory() == category)
                .filter(s -> s.getStatus() != SeatStatus.OCCUPIED)
                .sorted(Comparator.comparing(Seat::getRow).thenComparingInt(Seat::getSeatNumber))
                .collect(Collectors.toList());

        return candidates.isEmpty() ? Optional.empty() : Optional.of(candidates.get(0));
    }

    public Seat allocateSeat(String eventId, String ticketId, TicketCategory category, String attendeeName) {
        Optional<Seat> seatOpt = findBestAvailableSeat(eventId, category);
        if (seatOpt.isEmpty()) {
            throw new IllegalStateException("No eligible seats available for category: " + category);
        }

        Seat seat = seatOpt.get();
        seat.setStatus(SeatStatus.OCCUPIED);
        seat.setTicketId(ticketId);
        seat.setAttendeeName(attendeeName);
        seatRepository.save(seat);

        Optional<Ticket> ticketOpt = ticketRepository.findById(ticketId);
        if (ticketOpt.isPresent()) {
            Ticket ticket = ticketOpt.get();
            ticket.setSeatId(seat.getId());
            ticketRepository.save(ticket);
        }

        return seat;
    }

    public Optional<Seat> assignSpecificSeat(String seatId, String ticketId, String attendeeName) {
        Optional<Seat> seatOpt = seatRepository.findById(seatId);
        if (seatOpt.isEmpty()) {
            return Optional.empty();
        }

        Seat seat = seatOpt.get();
        if (seat.getStatus() == SeatStatus.OCCUPIED) {
            throw new IllegalStateException("Seat is already occupied: " + seat.getLabel());
        }

        seat.setStatus(SeatStatus.OCCUPIED);
        seat.setTicketId(ticketId);
        seat.setAttendeeName(attendeeName);
        seatRepository.save(seat);

        Optional<Ticket> ticketOpt = ticketRepository.findById(ticketId);
        if (ticketOpt.isPresent()) {
            Ticket ticket = ticketOpt.get();
            ticket.setSeatId(seat.getId());
            ticketRepository.save(ticket);
        }

        return Optional.of(seat);
    }
}
