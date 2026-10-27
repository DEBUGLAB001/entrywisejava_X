package com.entrywise.service;

import com.entrywise.model.*;
import com.entrywise.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdmissionCapacityService {
    private final EventRepository eventRepository;
    private final CheckInRepository checkInRepository;
    private final EntryGateRepository gateRepository;
    private final QueueRepository queueRepository;

    public AdmissionCapacityService(EventRepository eventRepository,
                                    CheckInRepository checkInRepository,
                                    EntryGateRepository gateRepository,
                                    QueueRepository queueRepository) {
        this.eventRepository = eventRepository;
        this.checkInRepository = checkInRepository;
        this.gateRepository = gateRepository;
        this.queueRepository = queueRepository;
    }

    public CapacityMetrics getCapacityMetrics(String eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found: " + eventId));

        long successfulAdmissions = checkInRepository.countByEventIdAndOutcome(eventId, AdmissionOutcome.ADMITTED);
        long totalAttempts = checkInRepository.countByEventId(eventId);
        long rejectedAdmissions = Math.max(0, totalAttempts - successfulAdmissions);

        int admitted = (int) successfulAdmissions;
        event.setAdmittedCount(admitted);

        int remaining = Math.max(0, event.getTotalCapacity() - event.getReservedBufferCapacity());
        event.setRemainingCapacity(remaining);
        eventRepository.save(event);

        double occupancy = event.getTotalCapacity() > 0
                ? ((double) admitted / event.getTotalCapacity()) * 100.0
                : 0.0;

        List<EntryGate> gates = gateRepository.findByEventId(eventId);
        int activeGates = (int) gates.stream().filter(g -> g.getStatus() == GateStatus.OPEN).count();

        List<QueueEntry> queue = queueRepository.findByEventId(eventId);
        int waitingInQueue = (int) queue.stream().filter(q -> q.getStatus() == QueueStatus.WAITING).count();

        return new CapacityMetrics(
                eventId,
                event.getTotalCapacity(),
                admitted,
                remaining,
                Math.round(occupancy * 10.0) / 10.0,
                activeGates,
                waitingInQueue,
                (int) successfulAdmissions,
                (int) rejectedAdmissions
        );
    }

    public void syncCapacity(String eventId) {
        getCapacityMetrics(eventId);
    }
}
