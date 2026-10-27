package com.entrywise.controller;

import com.entrywise.dto.SeatAllocationRequest;
import com.entrywise.model.Seat;
import com.entrywise.service.SeatAllocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/seating")
public class SeatingController {
    private final SeatAllocationService seatAllocationService;

    public SeatingController(SeatAllocationService seatAllocationService) {
        this.seatAllocationService = seatAllocationService;
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<List<Seat>> getSeatingLayout(@PathVariable String eventId) {
        return ResponseEntity.ok(seatAllocationService.getSeatingLayout(eventId));
    }

    @PostMapping("/allocate")
    public ResponseEntity<?> autoAllocateSeat(@RequestBody SeatAllocationRequest request) {
        try {
            Seat allocated = seatAllocationService.allocateSeat(
                    request.getEventId(),
                    request.getTicketId(),
                    request.getCategory(),
                    "Admitted Attendee"
            );
            return ResponseEntity.ok(allocated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/assign")
    public ResponseEntity<?> assignSpecificSeat(@RequestBody Map<String, String> payload) {
        String seatId = payload.get("seatId");
        String ticketId = payload.get("ticketId");
        String attendeeName = payload.get("attendeeName");

        try {
            return seatAllocationService.assignSpecificSeat(seatId, ticketId, attendeeName)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
