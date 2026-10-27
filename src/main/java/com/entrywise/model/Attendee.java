package com.entrywise.model;

public class Attendee {
    private String id;
    private String name;
    private String email;
    private String eventId;
    private String ticketId;
    private String assignedSeat;
    private AdmissionStatus admissionStatus;

    public Attendee() {
    }

    public Attendee(String id, String name, String email, String eventId,
                    String ticketId, String assignedSeat, AdmissionStatus admissionStatus) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.eventId = eventId;
        this.ticketId = ticketId;
        this.assignedSeat = assignedSeat;
        this.admissionStatus = admissionStatus;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getAssignedSeat() {
        return assignedSeat;
    }

    public void setAssignedSeat(String assignedSeat) {
        this.assignedSeat = assignedSeat;
    }

    public AdmissionStatus getAdmissionStatus() {
        return admissionStatus;
    }

    public void setAdmissionStatus(AdmissionStatus admissionStatus) {
        this.admissionStatus = admissionStatus;
    }
}
