package com.entrywise.repository;

import com.entrywise.model.Event;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class EventRepository {
    private final Map<String, Event> events = new ConcurrentHashMap<>();

    public List<Event> findAll() {
        return new ArrayList<>(events.values());
    }

    public Optional<Event> findById(String id) {
        return Optional.ofNullable(events.get(id));
    }

    public Event save(Event event) {
        events.put(event.getId(), event);
        return event;
    }

    public void clear() {
        events.clear();
    }
}
