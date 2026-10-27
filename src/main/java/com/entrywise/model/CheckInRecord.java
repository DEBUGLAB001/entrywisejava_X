package com.entrywise.model;

import java.time.LocalDateTime;

public class CheckInRecord {
    private String id;
    private String eventId;
    private String ticketId;
    private String ticketCode;
    private String attendeeName;
    private String gateId;
    private AdmissionOutcome outcome;
    private LocalDateTime timestamp;
    private String rejectionReason;

    public CheckInRecord() {
    }

    public CheckInRecord(String id, String eventId, String ticketId, String ticketCode,
                         String attendeeName, String gateId, AdmissionOutcome outcome,
                         LocalDateTime timestamp, String rejectionReason) {
        this.id = id;
        this.eventId = eventId;
        this.ticketId = ticketId;
        this.ticketCode = ticketCode;
        this.attendeeName = attendeeName;
        this.gateId = gateId;
        this.outcome = outcome;
        this.timestamp = timestamp;
        this.rejectionReason = rejectionReason;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getTicketCode() {
        return ticketCode;
    }

    public void setTicketCode(String ticketCode) {
        this.ticketCode = ticketCode;
    }

    public String getAttendeeName() {
        return attendeeName;
    }

    public void setAttendeeName(String attendeeName) {
        this.attendeeName = attendeeName;
    }

    public String getGateId() {
        return gateId;
    }

    public void setGateId(String gateId) {
        this.gateId = gateId;
    }

    public AdmissionOutcome getOutcome() {
        return outcome;
    }

    public void setOutcome(AdmissionOutcome outcome) {
        this.outcome = outcome;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}
