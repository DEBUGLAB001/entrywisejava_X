package com.entrywise.model;

public class CapacityMetrics {
    private String eventId;
    private int totalCapacity;
    private int admittedCount;
    private int remainingCapacity;
    private double occupancyPercentage;
    private int activeGatesCount;
    private int waitingQueueCount;
    private int successfulAdmissions;
    private int rejectedAdmissions;

    public CapacityMetrics() {
    }

    public CapacityMetrics(String eventId, int totalCapacity, int admittedCount,
                           int remainingCapacity, double occupancyPercentage,
                           int activeGatesCount, int waitingQueueCount,
                           int successfulAdmissions, int rejectedAdmissions) {
        this.eventId = eventId;
        this.totalCapacity = totalCapacity;
        this.admittedCount = admittedCount;
        this.remainingCapacity = remainingCapacity;
        this.occupancyPercentage = occupancyPercentage;
        this.activeGatesCount = activeGatesCount;
        this.waitingQueueCount = waitingQueueCount;
        this.successfulAdmissions = successfulAdmissions;
        this.rejectedAdmissions = rejectedAdmissions;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
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

    public double getOccupancyPercentage() {
        return occupancyPercentage;
    }

    public void setOccupancyPercentage(double occupancyPercentage) {
        this.occupancyPercentage = occupancyPercentage;
    }

    public int getActiveGatesCount() {
        return activeGatesCount;
    }

    public void setActiveGatesCount(int activeGatesCount) {
        this.activeGatesCount = activeGatesCount;
    }

    public int getWaitingQueueCount() {
        return waitingQueueCount;
    }

    public void setWaitingQueueCount(int waitingQueueCount) {
        this.waitingQueueCount = waitingQueueCount;
    }

    public int getSuccessfulAdmissions() {
        return successfulAdmissions;
    }

    public void setSuccessfulAdmissions(int successfulAdmissions) {
        this.successfulAdmissions = successfulAdmissions;
    }

    public int getRejectedAdmissions() {
        return rejectedAdmissions;
    }

    public void setRejectedAdmissions(int rejectedAdmissions) {
        this.rejectedAdmissions = rejectedAdmissions;
    }
}
