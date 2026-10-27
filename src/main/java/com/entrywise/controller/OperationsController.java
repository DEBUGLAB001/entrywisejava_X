package com.entrywise.controller;

import com.entrywise.model.CapacityMetrics;
import com.entrywise.repository.DataSeeder;
import com.entrywise.service.AdmissionCapacityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class OperationsController {
    private final AdmissionCapacityService capacityService;
    private final DataSeeder dataSeeder;

    public OperationsController(AdmissionCapacityService capacityService,
                                DataSeeder dataSeeder) {
        this.capacityService = capacityService;
        this.dataSeeder = dataSeeder;
    }

    @GetMapping("/metrics/{eventId}")
    public ResponseEntity<CapacityMetrics> getMetrics(@PathVariable String eventId) {
        return ResponseEntity.ok(capacityService.getCapacityMetrics(eventId));
    }

    @PostMapping("/reset")
    public ResponseEntity<Map<String, Object>> resetData() {
        dataSeeder.resetData();
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Demonstration scenario reset successfully"
        ));
    }
}
