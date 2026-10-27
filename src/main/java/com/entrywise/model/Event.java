package com.entrywise.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Event {
    private String id;
    private String name;
    private String venue;
    private LocalDateTime eventDate;
    private LocalDateTime admissionWindowStart;
    private LocalDateTime admissionWindowEnd;
    private boolean assignedSeating;
    private List<TicketCategory> ticketCategories = new ArrayList<>();
    private int totalCapacity;
    private int admittedCount;
    private int remainingCapacity;
    private int reservedBufferCapacity;
    private EventStatus status;

    public Event() {
    }

    public Event(String id, String name, String venue, LocalDateTime eventDate,
                 LocalDateTime admissionWindowStart, LocalDateTime admissionWindowEnd,
                 boolean assignedSeating, List<TicketCategory> ticketCategories,
                 int totalCapacity, int admittedCount, int reservedBufferCapacity, EventStatus status) {
        this.id = id;
        this.name = name;
        this.venue = venue;
        this.eventDate = eventDate;
        this.admissionWindowStart = admissionWindowStart;
        this.admissionWindowEnd = admissionWindowEnd;
        this.assignedSeating = assignedSeating;
        this.ticketCategories = ticketCategories != null ? new ArrayList<>(ticketCategories) : new ArrayList<>();
        this.totalCapacity = totalCapacity;
        this.admittedCount = admittedCount;
        this.reservedBufferCapacity = reservedBufferCapacity;
        this.remainingCapacity = Math.max(0, totalCapacity - admittedCount);
        this.status = status;
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

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }

    public LocalDateTime getAdmissionWindowStart() {
        return admissionWindowStart;
    }

    public void setAdmissionWindowStart(LocalDateTime admissionWindowStart) {
        this.admissionWindowStart = admissionWindowStart;
    }

    public LocalDateTime getAdmissionWindowEnd() {
        return admissionWindowEnd;
    }

    public void setAdmissionWindowEnd(LocalDateTime admissionWindowEnd) {
        this.admissionWindowEnd = admissionWindowEnd;
    }

    public boolean isAssignedSeating() {
        return assignedSeating;
    }

    public void setAssignedSeating(boolean assignedSeating) {
        this.assignedSeating = assignedSeating;
    }

    public List<TicketCategory> getTicketCategories() {
        return ticketCategories;
    }

    public void setTicketCategories(List<TicketCategory> ticketCategories) {
        this.ticketCategories = ticketCategories;
    }

    public int getTotalCapacity() {
        return totalCapacity;
    }

    public void setTotalCapacity(int totalCapacity) {
        this.totalCapacity = totalCapacity;
    }

    public int getAdmittedCount() {
        return admittedCount;
    }

    public void setAdmittedCount(int admittedCount) {
        this.admittedCount = admittedCount;
    }

    public int getRemainingCapacity() {
        return remainingCapacity;
    }

    public void setRemainingCapacity(int remainingCapacity) {
        this.remainingCapacity = remainingCapacity;
    }

    public int getReservedBufferCapacity() {
        return reservedBufferCapacity;
    }

    public void setReservedBufferCapacity(int reservedBufferCapacity) {
        this.reservedBufferCapacity = reservedBufferCapacity;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }
}
