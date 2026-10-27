package com.entrywise.repository;

import com.entrywise.model.Seat;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class SeatRepository {
    private final Map<String, Seat> seats = new ConcurrentHashMap<>();

    public List<Seat> findAll() {
        return new ArrayList<>(seats.values());
    }

    public List<Seat> findByEventId(String eventId) {
        return seats.values().stream()
                .filter(s -> Objects.equals(s.getEventId(), eventId))
                .sorted(Comparator.comparing(Seat::getRow).thenComparingInt(Seat::getSeatNumber))
                .collect(Collectors.toList());
    }

    public Optional<Seat> findById(String id) {
        return Optional.ofNullable(seats.get(id));
    }

    public Seat save(Seat seat) {
        seats.put(seat.getId(), seat);
        return seat;
    }

    public void clear() {
        seats.clear();
    }
}
