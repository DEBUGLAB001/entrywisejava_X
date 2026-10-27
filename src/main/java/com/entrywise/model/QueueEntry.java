package com.entrywise.model;

import java.time.LocalDateTime;

public class QueueEntry {
    private String id;
    private String eventId;
    private String gateId;
    private String ticketId;
    private String ticketCode;
    private String attendeeName;
    private PriorityTier priorityTier;
    private long sequenceNumber;
    private LocalDateTime joinedAt;
    private QueueStatus status;
    private int estimatedWaitMinutes;

    public QueueEntry() {
    }

    public QueueEntry(String id, String eventId, String gateId, String ticketId,
                      String ticketCode, String attendeeName, PriorityTier priorityTier,
                      long sequenceNumber, LocalDateTime joinedAt, QueueStatus status,
                      int estimatedWaitMinutes) {
        this.id = id;
        this.eventId = eventId;
        this.gateId = gateId;
        this.ticketId = ticketId;
        this.ticketCode = ticketCode;
        this.attendeeName = attendeeName;
        this.priorityTier = priorityTier;
        this.sequenceNumber = sequenceNumber;
        this.joinedAt = joinedAt;
        this.status = status;
        this.estimatedWaitMinutes = estimatedWaitMinutes;
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

    public String getGateId() {
        return gateId;
    }

    public void setGateId(String gateId) {
        this.gateId = gateId;
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

    public PriorityTier getPriorityTier() {
        return priorityTier;
    }

    public void setPriorityTier(PriorityTier priorityTier) {
        this.priorityTier = priorityTier;
    }

    public long getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }

    public QueueStatus getStatus() {
        return status;
    }

    public void setStatus(QueueStatus status) {
        this.status = status;
    }

    public int getEstimatedWaitMinutes() {
        return estimatedWaitMinutes;
    }

    public void setEstimatedWaitMinutes(int estimatedWaitMinutes) {
        this.estimatedWaitMinutes = estimatedWaitMinutes;
    }
}
