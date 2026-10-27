package com.entrywise.controller;

import com.entrywise.dto.QueueEnqueueRequest;
import com.entrywise.model.QueueEntry;
import com.entrywise.service.EntryQueueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/queue")
public class QueueController {
    private final EntryQueueService queueService;

    public QueueController(EntryQueueService queueService) {
        this.queueService = queueService;
    }

    @GetMapping("/{gateId}")
    public ResponseEntity<List<QueueEntry>> getQueueForGate(@PathVariable String gateId) {
        return ResponseEntity.ok(queueService.getOrderedQueueForGate(gateId));
    }

    @PostMapping("/{gateId}/process-next")
    public ResponseEntity<?> processNext(@PathVariable String gateId) {
        return queueService.processNextAttendee(gateId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @PostMapping("/enqueue")
    public ResponseEntity<?> enqueueAttendee(@RequestBody QueueEnqueueRequest request) {
        try {
            QueueEntry entry = queueService.enqueueAttendee(
                    request.getEventId(),
                    request.getGateId(),
                    request.getTicketCode(),
                    null
            );
            return ResponseEntity.ok(entry);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{entryId}/cancel")
    public ResponseEntity<?> cancelEntry(@PathVariable String entryId) {
        return queueService.cancelQueueEntry(entryId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
