package com.entrywise.controller;

import com.entrywise.dto.AdmissionRequest;
import com.entrywise.model.CheckInRecord;
import com.entrywise.service.CheckInHistoryService;
import com.entrywise.service.TicketCheckInService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CheckInController {
    private final TicketCheckInService checkInService;
    private final CheckInHistoryService historyService;

    public CheckInController(TicketCheckInService checkInService,
                             CheckInHistoryService historyService) {
        this.checkInService = checkInService;
        this.historyService = historyService;
    }

    @PostMapping("/checkin")
    public ResponseEntity<CheckInRecord> checkIn(@RequestBody AdmissionRequest request) {
        CheckInRecord record = checkInService.checkIn(
                request.getTicketCode(),
                request.getEventId(),
                request.getGateId()
        );
        return ResponseEntity.ok(record);
    }

    @GetMapping("/history")
    public ResponseEntity<List<CheckInRecord>> getHistory(@RequestParam(required = false) String eventId) {
        if (eventId != null && !eventId.isBlank()) {
            return ResponseEntity.ok(historyService.getHistoryForEvent(eventId));
        }
        return ResponseEntity.ok(historyService.getAllHistory());
    }

    @PostMapping("/history/{recordId}/reverse")
    public ResponseEntity<?> reverseAdmission(@PathVariable String recordId) {
        return historyService.reverseAdmission(recordId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
