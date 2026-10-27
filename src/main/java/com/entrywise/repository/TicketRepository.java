package com.entrywise.repository;

import com.entrywise.model.Ticket;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class TicketRepository {
    private final Map<String, Ticket> ticketsById = new ConcurrentHashMap<>();
    private final Map<String, String> ticketIdByCode = new ConcurrentHashMap<>();

    public List<Ticket> findAll() {
        return new ArrayList<>(ticketsById.values());
    }

    public List<Ticket> findByEventId(String eventId) {
        return ticketsById.values().stream()
                .filter(t -> Objects.equals(t.getEventId(), eventId))
                .collect(Collectors.toList());
    }

    public Optional<Ticket> findById(String id) {
        return Optional.ofNullable(ticketsById.get(id));
    }

    public Optional<Ticket> findByCode(String code) {
        if (code == null) return Optional.empty();
        String normalized = code.trim().toUpperCase();
        String id = ticketIdByCode.get(normalized);
        if (id != null) {
            return Optional.ofNullable(ticketsById.get(id));
        }
        return ticketsById.values().stream()
                .filter(t -> t.getTicketCode() != null && t.getTicketCode().equalsIgnoreCase(normalized))
                .findFirst();
    }

    public Ticket save(Ticket ticket) {
        ticketsById.put(ticket.getId(), ticket);
        if (ticket.getTicketCode() != null) {
            ticketIdByCode.put(ticket.getTicketCode().trim().toUpperCase(), ticket.getId());
        }
        return ticket;
    }

    public void clear() {
        ticketsById.clear();
        ticketIdByCode.clear();
    }
}
