package com.entrywise.model;

public class Seat {
    private String id;
    private String eventId;
    private String section;
    private String row;
    private int seatNumber;
    private TicketCategory category;
    private SeatStatus status;
    private String ticketId;
    private String attendeeName;

    public Seat() {
    }

    public Seat(String id, String eventId, String section, String row,
                int seatNumber, TicketCategory category, SeatStatus status,
                String ticketId, String attendeeName) {
        this.id = id;
        this.eventId = eventId;
        this.section = section;
        this.row = row;
        this.seatNumber = seatNumber;
        this.category = category;
        this.status = status;
        this.ticketId = ticketId;
        this.attendeeName = attendeeName;
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

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getRow() {
        return row;
    }

    public void setRow(String row) {
        this.row = row;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }

    public TicketCategory getCategory() {
        return category;
    }

    public void setCategory(TicketCategory category) {
        this.category = category;
    }

    public SeatStatus getStatus() {
        return status;
    }

    public void setStatus(SeatStatus status) {
        this.status = status;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getAttendeeName() {
        return attendeeName;
    }

    public void setAttendeeName(String attendeeName) {
        this.attendeeName = attendeeName;
    }

    public String getLabel() {
        return row + "-" + (seatNumber < 10 ? "0" + seatNumber : seatNumber);
    }
}
