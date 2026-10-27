package com.entrywise.model;

import java.time.LocalDateTime;

public class Ticket {
    private String id;
    private String ticketCode;
    private String eventId;
    private String attendeeId;
    private TicketCategory category;
    private String seatId;
    private TicketStatus status;
    private AdmissionStatus admissionStatus;
    private LocalDateTime checkInTimestamp;
    private String gateId;

    public Ticket() {
    }

    public Ticket(String id, String ticketCode, String eventId, String attendeeId,
                  TicketCategory category, String seatId, TicketStatus status,
                  AdmissionStatus admissionStatus, LocalDateTime checkInTimestamp, String gateId) {
        this.id = id;
        this.ticketCode = ticketCode;
        this.eventId = eventId;
        this.attendeeId = attendeeId;
        this.category = category;
        this.seatId = seatId;
        this.status = status;
        this.admissionStatus = admissionStatus;
        this.checkInTimestamp = checkInTimestamp;
        this.gateId = gateId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getAttendeeId() {
        return attendeeId;
    }

    public void setAttendeeId(String attendeeId) {
        this.attendeeId = attendeeId;
    }

    public TicketCategory getCategory() {
        return category;
    }

    public void setCategory(TicketCategory category) {
        this.category = category;
    }

    public String getSeatId() {
        return seatId;
    }

    public void setSeatId(String seatId) {
        this.seatId = seatId;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public AdmissionStatus getAdmissionStatus() {
        return admissionStatus;
    }

    public void setAdmissionStatus(AdmissionStatus admissionStatus) {
        this.admissionStatus = admissionStatus;
    }

    public LocalDateTime getCheckInTimestamp() {
        return checkInTimestamp;
    }

    public void setCheckInTimestamp(LocalDateTime checkInTimestamp) {
        this.checkInTimestamp = checkInTimestamp;
    }

    public String getGateId() {
        return gateId;
    }

    public void setGateId(String gateId) {
        this.gateId = gateId;
    }
}
