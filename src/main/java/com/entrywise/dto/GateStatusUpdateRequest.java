package com.entrywise.dto;

import com.entrywise.model.GateStatus;

public class GateStatusUpdateRequest {
    private GateStatus status;

    public GateStatusUpdateRequest() {
    }

    public GateStatusUpdateRequest(GateStatus status) {
        this.status = status;
    }

    public GateStatus getStatus() {
        return status;
    }

    public void setStatus(GateStatus status) {
        this.status = status;
    }
}
