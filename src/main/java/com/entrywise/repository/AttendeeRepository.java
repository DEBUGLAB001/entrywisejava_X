package com.entrywise.repository;

import com.entrywise.model.Attendee;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class AttendeeRepository {
    private final Map<String, Attendee> attendees = new ConcurrentHashMap<>();

    public List<Attendee> findAll() {
        return new ArrayList<>(attendees.values());
    }

    public List<Attendee> findByEventId(String eventId) {
        return attendees.values().stream()
                .filter(a -> Objects.equals(a.getEventId(), eventId))
                .collect(Collectors.toList());
    }

    public Optional<Attendee> findById(String id) {
        return Optional.ofNullable(attendees.get(id));
    }

    public Attendee save(Attendee attendee) {
        attendees.put(attendee.getId(), attendee);
        return attendee;
    }

    public void clear() {
        attendees.clear();
    }
}
