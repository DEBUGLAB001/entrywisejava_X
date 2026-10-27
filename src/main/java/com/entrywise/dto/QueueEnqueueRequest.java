package com.entrywise.dto;

public class QueueEnqueueRequest {
    private String eventId;
    private String gateId;
    private String ticketCode;

    public QueueEnqueueRequest() {
    }

    public QueueEnqueueRequest(String eventId, String gateId, String ticketCode) {
        this.eventId = eventId;
        this.gateId = gateId;
        this.ticketCode = ticketCode;
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

    public String getTicketCode() {
        return ticketCode;
    }

    public void setTicketCode(String ticketCode) {
        this.ticketCode = ticketCode;
    }
}
