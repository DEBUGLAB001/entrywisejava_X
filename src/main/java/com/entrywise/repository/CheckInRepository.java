package com.entrywise.repository;

import com.entrywise.model.AdmissionOutcome;
import com.entrywise.model.CheckInRecord;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class CheckInRepository {
    private final Map<String, CheckInRecord> records = new ConcurrentHashMap<>();

    public List<CheckInRecord> findAll() {
        return records.values().stream()
                .sorted(Comparator.comparing(CheckInRecord::getTimestamp).reversed())
                .collect(Collectors.toList());
    }

    public List<CheckInRecord> findByEventId(String eventId) {
        return records.values().stream()
                .filter(r -> Objects.equals(r.getEventId(), eventId))
                .sorted(Comparator.comparing(CheckInRecord::getTimestamp).reversed())
                .collect(Collectors.toList());
    }

    public List<CheckInRecord> findByGateId(String gateId) {
        return records.values().stream()
                .filter(r -> Objects.equals(r.getGateId(), gateId))
                .sorted(Comparator.comparing(CheckInRecord::getTimestamp).reversed())
                .collect(Collectors.toList());
    }

    public Optional<CheckInRecord> findById(String id) {
        return Optional.ofNullable(records.get(id));
    }

    public long countByEventIdAndOutcome(String eventId, AdmissionOutcome outcome) {
        return records.values().stream()
                .filter(r -> Objects.equals(r.getEventId(), eventId) && r.getOutcome() == outcome)
                .count();
    }

    public long countByEventId(String eventId) {
        return records.values().stream()
                .filter(r -> Objects.equals(r.getEventId(), eventId))
                .count();
    }

    public CheckInRecord save(CheckInRecord record) {
        records.put(record.getId(), record);
        return record;
    }

    public void clear() {
        records.clear();
    }
}
