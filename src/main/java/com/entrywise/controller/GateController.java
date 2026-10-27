package com.entrywise.controller;

import com.entrywise.dto.GateStatusUpdateRequest;
import com.entrywise.model.EntryGate;
import com.entrywise.service.GateOperationsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gates")
public class GateController {
    private final GateOperationsService gateOperationsService;

    public GateController(GateOperationsService gateOperationsService) {
        this.gateOperationsService = gateOperationsService;
    }

    @GetMapping
    public ResponseEntity<List<EntryGate>> getGates(@RequestParam(required = false) String eventId) {
        if (eventId != null && !eventId.isBlank()) {
            return ResponseEntity.ok(gateOperationsService.getGatesForEvent(eventId));
        }
        return ResponseEntity.ok(gateOperationsService.getGatesForEvent("EVT-101"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntryGate> getGateById(@PathVariable String id) {
        return gateOperationsService.getGateById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<EntryGate> updateGateStatus(@PathVariable String id,
                                                      @RequestBody GateStatusUpdateRequest request) {
        return ResponseEntity.ok(gateOperationsService.updateGateStatus(id, request.getStatus()));
    }
}
