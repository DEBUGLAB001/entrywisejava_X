package com.entrywise.dto;

import com.entrywise.model.TicketCategory;

public class SeatAllocationRequest {
    private String eventId;
    private String ticketId;
    private TicketCategory category;
    private String preferredSeatId;

    public SeatAllocationRequest() {
    }

    public SeatAllocationRequest(String eventId, String ticketId, TicketCategory category, String preferredSeatId) {
        this.eventId = eventId;
        this.ticketId = ticketId;
        this.category = category;
        this.preferredSeatId = preferredSeatId;
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

    public TicketCategory getCategory() {
        return category;
    }

    public void setCategory(TicketCategory category) {
        this.category = category;
    }

    public String getPreferredSeatId() {
        return preferredSeatId;
    }

    public void setPreferredSeatId(String preferredSeatId) {
        this.preferredSeatId = preferredSeatId;
    }
}
