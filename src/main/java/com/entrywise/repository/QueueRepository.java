package com.entrywise.repository;

import com.entrywise.model.QueueEntry;
import com.entrywise.model.QueueStatus;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class QueueRepository {
    private final Map<String, QueueEntry> entries = new ConcurrentHashMap<>();

    public List<QueueEntry> findAll() {
        return new ArrayList<>(entries.values());
    }

    public List<QueueEntry> findByGateId(String gateId) {
        return entries.values().stream()
                .filter(e -> Objects.equals(e.getGateId(), gateId))
                .sorted(Comparator.comparingLong(QueueEntry::getSequenceNumber))
                .collect(Collectors.toList());
    }

    public List<QueueEntry> findWaitingByGateId(String gateId) {
        return entries.values().stream()
                .filter(e -> Objects.equals(e.getGateId(), gateId) && e.getStatus() == QueueStatus.WAITING)
                .collect(Collectors.toList());
    }

    public List<QueueEntry> findByEventId(String eventId) {
        return entries.values().stream()
                .filter(e -> Objects.equals(e.getEventId(), eventId))
                .collect(Collectors.toList());
    }

    public Optional<QueueEntry> findById(String id) {
        return Optional.ofNullable(entries.get(id));
    }

    public QueueEntry save(QueueEntry entry) {
        entries.put(entry.getId(), entry);
        return entry;
    }

    public void clear() {
        entries.clear();
    }
}
