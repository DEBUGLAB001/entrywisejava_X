package com.entrywise.model;

import java.util.HashSet;
import java.util.Set;

public class EntryGate {
    private String id;
    private String name;
    private String eventId;
    private GateStatus status;
    private Set<TicketCategory> allowedCategories = new HashSet<>();
    private int totalAdmissions;

    public EntryGate() {
    }

    public EntryGate(String id, String name, String eventId, GateStatus status,
                     Set<TicketCategory> allowedCategories, int totalAdmissions) {
        this.id = id;
        this.name = name;
        this.eventId = eventId;
        this.status = status;
        this.allowedCategories = allowedCategories != null ? new HashSet<>(allowedCategories) : new HashSet<>();
        this.totalAdmissions = totalAdmissions;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public GateStatus getStatus() {
        return status;
    }

    public void setStatus(GateStatus status) {
        this.status = status;
    }

    public Set<TicketCategory> getAllowedCategories() {
        return allowedCategories;
    }

    public void setAllowedCategories(Set<TicketCategory> allowedCategories) {
        this.allowedCategories = allowedCategories != null ? new HashSet<>(allowedCategories) : new HashSet<>();
    }

    public int getTotalAdmissions() {
        return totalAdmissions;
    }

    public void setTotalAdmissions(int totalAdmissions) {
        this.totalAdmissions = totalAdmissions;
    }
}
