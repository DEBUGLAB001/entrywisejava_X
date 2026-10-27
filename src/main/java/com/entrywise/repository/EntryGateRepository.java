package com.entrywise.repository;

import com.entrywise.model.EntryGate;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class EntryGateRepository {
    private final Map<String, EntryGate> gates = new ConcurrentHashMap<>();

    public List<EntryGate> findAll() {
        return new ArrayList<>(gates.values());
    }

    public List<EntryGate> findByEventId(String eventId) {
        return gates.values().stream()
                .filter(g -> Objects.equals(g.getEventId(), eventId))
                .sorted(Comparator.comparing(EntryGate::getId))
                .collect(Collectors.toList());
    }

    public Optional<EntryGate> findById(String id) {
        return Optional.ofNullable(gates.get(id));
    }

    public EntryGate save(EntryGate gate) {
        gates.put(gate.getId(), gate);
        return gate;
    }

    public void clear() {
        gates.clear();
    }
}
