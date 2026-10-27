package com.entrywise.dto;

public class TicketValidationRequest {
    private String ticketCode;
    private String eventId;
    private String gateId;

    public TicketValidationRequest() {
    }

    public TicketValidationRequest(String ticketCode, String eventId, String gateId) {
        this.ticketCode = ticketCode;
        this.eventId = eventId;
        this.gateId = gateId;
    }

    public String getTicketCode() {
        return ticketCode;
    }

    public void setTicketCode(String ticketCode) {
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
}
