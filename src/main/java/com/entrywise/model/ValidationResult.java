package com.entrywise.model;

public class ValidationResult {
    private boolean valid;
    private String status; // "VALID", "ALREADY_CHECKED_IN", "CANCELLED", "SUSPENDED", "INELIGIBLE_GATE", "INVALID_CODE", "EVENT_MISMATCH"
    private String message;
    private Ticket ticket;
    private Attendee attendee;
    private Seat seat;
    private EntryGate gate;

    public ValidationResult() {
    }

    public ValidationResult(boolean valid, String status, String message,
                            Ticket ticket, Attendee attendee, Seat seat, EntryGate gate) {
        this.valid = valid;
        this.status = status;
        this.message = message;
        this.ticket = ticket;
        this.attendee = attendee;
        this.seat = seat;
        this.gate = gate;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public Attendee getAttendee() {
        return attendee;
    }

    public void setAttendee(Attendee attendee) {
        this.attendee = attendee;
    }

    public Seat getSeat() {
        return seat;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }

    public EntryGate getGate() {
        return gate;
    }

    public void setGate(EntryGate gate) {
        this.gate = gate;
    }
}
