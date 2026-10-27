package com.entrywise.service;

import com.entrywise.model.EntryGate;
import com.entrywise.model.GateStatus;
import com.entrywise.repository.EntryGateRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GateOperationsService {
    private final EntryGateRepository gateRepository;

    public GateOperationsService(EntryGateRepository gateRepository) {
        this.gateRepository = gateRepository;
    }

    public List<EntryGate> getGatesForEvent(String eventId) {
        return gateRepository.findByEventId(eventId);
    }

    public Optional<EntryGate> getGateById(String gateId) {
        return gateRepository.findById(gateId);
    }

    public EntryGate updateGateStatus(String gateId, GateStatus status) {
        EntryGate gate = gateRepository.findById(gateId)
                .orElseThrow(() -> new IllegalArgumentException("Gate not found: " + gateId));
        gate.setStatus(status);
        return gateRepository.save(gate);
    }
}
