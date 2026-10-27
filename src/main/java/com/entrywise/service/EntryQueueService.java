package com.entrywise.service;

import com.entrywise.model.*;
import com.entrywise.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class EntryQueueService {
    private final QueueRepository queueRepository;
    private final TicketRepository ticketRepository;
    private final EntryGateRepository gateRepository;
    private final AtomicLong sequenceGenerator = new AtomicLong(100);

    public EntryQueueService(QueueRepository queueRepository,
                             TicketRepository ticketRepository,
                             EntryGateRepository gateRepository) {
        this.queueRepository = queueRepository;
        this.ticketRepository = ticketRepository;
        this.gateRepository = gateRepository;
    }

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

    public List<QueueEntry> getOrderedQueueForGate(String gateId) {
        return queueRepository.findWaitingByGateId(gateId).stream()
                .filter(entry -> entry.getStatus() == QueueStatus.WAITING)
                .sorted(getQueuePriorityComparator())
                .collect(Collectors.toList());
    }

    public Optional<QueueEntry> findNextEligibleEntry(String gateId) {
        List<QueueEntry> ordered = getOrderedQueueForGate(gateId);
        return ordered.isEmpty() ? Optional.empty() : Optional.of(ordered.get(0));
    }

    public QueueEntry enqueueAttendee(String eventId, String gateId, String ticketCode, String attendeeName) {
        Optional<Ticket> ticketOpt = ticketRepository.findByCode(ticketCode);
        if (ticketOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid ticket code: " + ticketCode);
        }
        Ticket ticket = ticketOpt.get();

        PriorityTier tier;
        if (ticket.getCategory() == TicketCategory.VIP) {
            tier = PriorityTier.VIP;
        } else if (ticket.getCategory() == TicketCategory.PREMIUM) {
            tier = PriorityTier.PRIORITY;
        } else {
            tier = PriorityTier.STANDARD;
        }

        long seq = sequenceGenerator.incrementAndGet();
        QueueEntry entry = new QueueEntry(
                "QUE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                eventId,
                gateId,
                ticket.getId(),
                ticket.getTicketCode(),
                attendeeName != null ? attendeeName : "Attendee",
                tier,
                seq,
                LocalDateTime.now(),
                QueueStatus.WAITING,
                (int) (seq % 15 + 2)
        );
        return queueRepository.save(entry);
    }

    public Optional<QueueEntry> processNextAttendee(String gateId) {
        Optional<QueueEntry> nextOpt = findNextEligibleEntry(gateId);
        if (nextOpt.isPresent()) {
            QueueEntry entry = nextOpt.get();
            entry.setStatus(QueueStatus.ADMITTED);
            queueRepository.save(entry);
            return Optional.of(entry);
        }
        return Optional.empty();
    }

    public Optional<QueueEntry> cancelQueueEntry(String entryId) {
        Optional<QueueEntry> entryOpt = queueRepository.findById(entryId);
        if (entryOpt.isPresent()) {
            QueueEntry entry = entryOpt.get();
            entry.setStatus(QueueStatus.CANCELLED);
            queueRepository.save(entry);
            return Optional.of(entry);
        }
        return Optional.empty();
    }
}
