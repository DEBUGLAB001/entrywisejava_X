package com.entrywise.service;

import com.entrywise.model.Event;
import com.entrywise.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {
    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Optional<Event> getEventById(String eventId) {
        return eventRepository.findById(eventId);
    }
}
